-- =========================================================================================
-- SURYA B2B FINTECH PL/PGSQL ENTERPRISE STORED PROCEDURES
-- Database: PostgreSQL (Target Schema: Prisma Public)
-- =========================================================================================

-- 1. Daily Interest Accrual on Used Credit lines
-- Runs daily via pg_cron or worker trigger to accumulate dynamic interest
CREATE OR REPLACE PROCEDURE accrue_daily_credit_interest()
LANGUAGE plpgsql
AS $$
DECLARE
    rec RECORD;
    daily_rate DECIMAL(15,8);
    interest_accrued DECIMAL(15,2);
BEGIN
    FOR rec IN 
        SELECT id, "userId", "usedCredit", "interestRate" 
        FROM "CreditWallet" 
        WHERE "isActive" = TRUE AND "usedCredit" > 0.00
    LOOP
        -- Daily Interest = (Used Credit * (Interest Rate / 100)) / 365
        daily_rate := (rec."interestRate" / 100.00) / 365.00;
        interest_accrued := rec."usedCredit" * daily_rate;

        IF interest_accrued > 0.01 THEN
            -- Adjust used credit with accrued interest
            UPDATE "CreditWallet"
            SET "usedCredit" = "usedCredit" + interest_accrued,
                "updatedAt" = NOW()
            WHERE id = rec.id;

            -- Log transaction to main ledger
            INSERT INTO "WalletTransaction" (
                id, "walletId", type, service, amount, description, status, "referenceId", "paymentMethod", "createdAt"
            ) VALUES (
                gen_random_uuid()::text,
                (SELECT id FROM "Wallet" WHERE "userId" = rec."userId" LIMIT 1),
                'DEBIT',
                'CREDIT_PAY',
                interest_accrued,
                'Daily credit interest accrual at ' || rec."interestRate" || '% APY',
                'SUCCESS',
                'INT-' || rec."userId" || '-' || EXTRACT(EPOCH FROM NOW())::text,
                'CREDIT_LINE',
                NOW()
            );
        END IF;
    END LOOP;
END;
$$;


-- 2. Real-Time Split Commission Matrix Router
-- Atomically processes money transfers (DMT) and BBPS bill pay. Splits commissions
-- in a 1:10 ratio between Platform, Super Distributor, and Distributor.
CREATE OR REPLACE FUNCTION route_transaction_commission(
    p_wallet_id VARCHAR,
    p_total_charge DECIMAL(15,2),
    p_service_type VARCHAR
)
RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    v_user_id VARCHAR;
    v_parent_id VARCHAR;
    v_grand_parent_id VARCHAR;
    v_dist_commission DECIMAL(15,2);
    v_super_dist_commission DECIMAL(15,2);
BEGIN
    -- Fetch transaction initiator
    SELECT "userId" INTO v_user_id FROM "Wallet" WHERE id = p_wallet_id;
    
    -- Fetch organizational hierarchy links
    SELECT "parentId" INTO v_parent_id FROM "User" WHERE id = v_user_id;
    
    IF v_parent_id IS NOT NULL THEN
        SELECT "parentId" INTO v_grand_parent_id FROM "User" WHERE id = v_parent_id;
    END IF;

    -- Apply split commission rules (e.g. 1.2% total commission rate)
    v_dist_commission := p_total_charge * 0.010; -- 1% distributor payout
    v_super_dist_commission := p_total_charge * 0.002; -- 0.2% super distributor payout

    -- 1. Disburse to direct Distributor parent node if active
    IF v_parent_id IS NOT NULL THEN
        UPDATE "Wallet"
        SET balance = balance + v_dist_commission,
            "commissionEarned" = "commissionEarned" + v_dist_commission,
            "updatedAt" = NOW()
        WHERE "userId" = v_parent_id;

        INSERT INTO "WalletTransaction" (
            id, "walletId", type, service, amount, description, status, "referenceId", "paymentMethod"
        ) VALUES (
            gen_random_uuid()::text,
            (SELECT id FROM "Wallet" WHERE "userId" = v_parent_id LIMIT 1),
            'COMMISSION',
            p_service_type::"ServiceType",
            v_dist_commission,
            'Indirect transaction routing commission share',
            'SUCCESS',
            'COMM-DIST-' || EXTRACT(EPOCH FROM NOW())::text,
            'WALLET'
        );
    END IF;

    -- 2. Disburse to Super Distributor grand-parent node if active
    IF v_grand_parent_id IS NOT NULL THEN
        UPDATE "Wallet"
        SET balance = balance + v_super_dist_commission,
            "commissionEarned" = "commissionEarned" + v_super_dist_commission,
            "updatedAt" = NOW()
        WHERE "userId" = v_grand_parent_id;

        INSERT INTO "WalletTransaction" (
            id, "walletId", type, service, amount, description, status, "referenceId", "paymentMethod"
        ) VALUES (
            gen_random_uuid()::text,
            (SELECT id FROM "Wallet" WHERE "userId" = v_grand_parent_id LIMIT 1),
            'COMMISSION',
            p_service_type::"ServiceType",
            v_super_dist_commission,
            'Super Distributor network override commission share',
            'SUCCESS',
            'COMM-SUDIST-' || EXTRACT(EPOCH FROM NOW())::text,
            'WALLET'
        );
    END IF;
END;
$$;


-- 3. Risk Alert Threshold Check Trigger
-- Warns platform admins when dynamic outstanding credit usage exceeds 90% of limit
CREATE OR REPLACE FUNCTION check_credit_limit_threshold()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_usage_ratio DECIMAL(5,2);
BEGIN
    IF NEW."creditLimit" > 0 THEN
        v_usage_ratio := (NEW."usedCredit" / NEW."creditLimit") * 100.00;
        
        IF v_usage_ratio >= 90.00 THEN
            -- Create high-priority in-app advisory notice to user
            INSERT INTO "Notification" (
                id, "userId", title, message, type, "isRead", "createdAt"
            ) VALUES (
                gen_random_uuid()::text,
                NEW."userId",
                'CRITICAL: Outstanding Credit Line Usage High',
                'Your dynamic credit outstanding has reached ' || v_usage_ratio || '% of limit. Repay to avoid service throttling.',
                'WARNING',
                FALSE,
                NOW()
            );

            -- Raise security log
            INSERT INTO "AuditLog" (
                id, "userId", action, "ipAddress", metadata, "createdAt"
            ) VALUES (
                gen_random_uuid()::text,
                NEW."userId",
                'HIGH_CREDIT_RISK_WARN',
                '0.0.0.0',
                '{"usage_percentage": ' || v_usage_ratio || ', "outstanding_amount": ' || NEW."usedCredit" || '}',
                NOW()
            );
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

-- Trigger configuration
DROP TRIGGER IF EXISTS trg_credit_limit_check ON "CreditWallet";
CREATE TRIGGER trg_credit_limit_check
AFTER UPDATE OF "usedCredit" ON "CreditWallet"
FOR EACH ROW
EXECUTE FUNCTION check_credit_limit_threshold();
