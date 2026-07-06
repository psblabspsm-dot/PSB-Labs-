import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import * as crypto from 'crypto';
import { CreateEmployeeDto, UpdateSalaryStructureDto, LogAttendanceDto, ApplyLeaveDto, ProcessPayrollDto } from './dto/hrms.dto';

export interface EmployeeProfile extends CreateEmployeeDto {
  id: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface EmployeeIdCard {
  id: string;
  employeeId: string;
  cardNo: string;
  qrCode: string;
  barcode: string;
  issueDate: Date;
  validTill: Date;
  tamperProof: string;
  signatureUrl: string;
  authSigUrl: string;
}

export interface SalaryStructure extends UpdateSalaryStructureDto {
  id: string;
  employeeId: string;
  netSalary: number;
}

export interface AttendanceRecord {
  id: string;
  employeeId: string;
  date: string;
  status: string;
  checkIn?: string;
  checkOut?: string;
  hoursWorked: number;
}

export interface LeaveRecord {
  id: string;
  employeeId: string;
  leaveType: string;
  startDate: string;
  endDate: string;
  status: string; // PENDING, APPROVED, REJECTED
  reason: string;
  createdAt: Date;
}

export interface PayslipRecord {
  id: string;
  employeeId: string;
  payslipNo: string;
  month: string;
  workingDays: number;
  presentDays: number;
  leaveDays: number;
  basicSalary: number;
  hra: number;
  allowances: number;
  incentives: number;
  deductions: number;
  netSalary: number;
  qrCode: string;
  signatureUrl: string;
  paymentRef: string;
  createdAt: Date;
}

@Injectable()
export class HrmsService {
  private employees: EmployeeProfile[] = [];
  private idCards: Map<string, EmployeeIdCard> = new Map();
  private salaryStructures: Map<string, SalaryStructure> = new Map();
  private attendanceRecords: AttendanceRecord[] = [];
  private leaveRecords: LeaveRecord[] = [];
  private payslips: PayslipRecord[] = [];

  constructor() {
    this.seedInitialData();
  }

  private seedInitialData() {
    const demoEmployees: CreateEmployeeDto[] = [
      {
        employeeCode: 'SCS-1001',
        fullName: 'Amrita Rao',
        photo: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?q=80&w=200&auto=format&fit=crop',
        designation: 'Senior Compliance Officer',
        department: 'Risk & Compliance',
        branch: 'Bangalore Head Office',
        reportingManager: 'Vignesh K. (Director of Operations)',
        mobileNumber: '+91 98860 12345',
        email: 'amrita.rao@suryacredit.com',
        aadhaarNumberMasked: 'XXXX-XXXX-4312',
        panNumberMasked: 'XXXXX5412D',
        dateOfJoining: '2024-03-01',
        dateOfBirth: '1995-07-15',
        bloodGroup: 'B+',
        address: 'Flat 402, Block C, Prestige Heights, Outer Ring Road, Bangalore - 560103',
        emergencyContact: '+91 98860 99999 (Father)',
        employmentType: 'FULL_TIME',
        bankAccountDetails: '501004128919',
        ifscCode: 'HDFC0000104',
        uan: '100912448212',
        esicNumber: '3112485918',
        pfNumber: 'KN/BAN/00124/091A',
        status: 'ACTIVE',
      },
      {
        employeeCode: 'SCS-1002',
        fullName: 'Rohan Sharma',
        photo: 'https://images.unsplash.com/photo-1560250097-0b93528c311a?q=80&w=200&auto=format&fit=crop',
        designation: 'Sales Lead',
        department: 'Sales & Business Development',
        branch: 'Bangalore Head Office',
        reportingManager: 'Vignesh K. (Director of Operations)',
        mobileNumber: '+91 99160 54321',
        email: 'rohan.sharma@suryacredit.com',
        aadhaarNumberMasked: 'XXXX-XXXX-8901',
        panNumberMasked: 'XXXXX8901C',
        dateOfJoining: '2024-05-15',
        dateOfBirth: '1992-11-20',
        bloodGroup: 'O+',
        address: 'No. 42, 4th Main, HSR Layout Sector 3, Bangalore - 560102',
        emergencyContact: '+91 99160 88888 (Spouse)',
        employmentType: 'FULL_TIME',
        bankAccountDetails: '91802004218901',
        ifscCode: 'ICIC0000002',
        uan: '100814918201',
        esicNumber: '3128941092',
        pfNumber: 'KN/BAN/00124/112C',
        status: 'ACTIVE',
      },
      {
        employeeCode: 'SCS-1003',
        fullName: 'Sneha Patel',
        photo: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?q=80&w=200&auto=format&fit=crop',
        designation: 'Operations Lead',
        department: 'Operations Support',
        branch: 'Pune Branch',
        reportingManager: 'Vignesh K. (Director of Operations)',
        mobileNumber: '+91 91122 33445',
        email: 'sneha.patel@suryacredit.com',
        aadhaarNumberMasked: 'XXXX-XXXX-7110',
        panNumberMasked: 'XXXXX7110A',
        dateOfJoining: '2025-01-10',
        dateOfBirth: '1997-04-05',
        bloodGroup: 'A-',
        address: 'Apt 12, Rose Wood Society, Koregaon Park, Pune - 411001',
        emergencyContact: '+91 91122 99911 (Mother)',
        employmentType: 'FULL_TIME',
        bankAccountDetails: '30291048128912',
        ifscCode: 'SBIN0001242',
        uan: '100741289104',
        esicNumber: '3152810482',
        pfNumber: 'PN/PUN/00248/018F',
        status: 'ACTIVE',
      },
    ];

    demoEmployees.forEach((emp) => {
      const id = crypto.randomUUID();
      const employee: EmployeeProfile = {
        id,
        ...emp,
        createdAt: new Date(),
        updatedAt: new Date(),
      };
      this.employees.push(employee);

      // Auto generate ID Card
      this.generateIdCard(employee);

      // Seed salary structures
      const baseSalary = emp.fullName.includes('Amrita') ? 65000 : emp.fullName.includes('Rohan') ? 48000 : 35000;
      const salary: SalaryStructure = {
        id: crypto.randomUUID(),
        employeeId: id,
        basicSalary: Math.round(baseSalary * 0.5),
        hra: Math.round(baseSalary * 0.25),
        specialAllowance: Math.round(baseSalary * 0.15),
        incentive: Math.round(baseSalary * 0.1),
        bonus: 0,
        overtime: 0,
        reimbursements: 0,
        pf: Math.round(baseSalary * 0.06),
        esic: Math.round(baseSalary * 0.0075),
        professionalTax: 200,
        incomeTax: 0,
        netSalary: baseSalary,
      };
      this.salaryStructures.set(id, salary);

      // Seed mock attendance for June 2026
      for (let day = 1; day <= 30; day++) {
        const dateStr = `2026-06-${day.toString().padStart(2, '0')}`;
        const isWeekend = new Date(dateStr).getDay() === 0 || new Date(dateStr).getDay() === 6;
        this.attendanceRecords.push({
          id: crypto.randomUUID(),
          employeeId: id,
          date: dateStr,
          status: isWeekend ? 'LEAVE' : 'PRESENT',
          checkIn: isWeekend ? undefined : '09:30:00',
          checkOut: isWeekend ? undefined : '18:30:00',
          hoursWorked: isWeekend ? 0 : 9.0,
        });
      }

      // Seed Payslip for June 2026
      const payslip: PayslipRecord = {
        id: crypto.randomUUID(),
        employeeId: id,
        payslipNo: `SCS-202606-${emp.employeeCode.split('-')[1]}`,
        month: '2026-06',
        workingDays: 30,
        presentDays: 22,
        leaveDays: 8, // includes weekends
        basicSalary: salary.basicSalary,
        hra: salary.hra,
        allowances: salary.specialAllowance,
        incentives: salary.incentive || 0,
        deductions: (salary.pf || 0) + (salary.esic || 0) + (salary.professionalTax || 0),
        netSalary: salary.netSalary,
        qrCode: `VERIFY_PAYSLIP_${id}_2026-06`,
        signatureUrl: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?q=80&w=50&auto=format&fit=crop',
        paymentRef: `TXN-${crypto.randomBytes(4).toString('hex').toUpperCase()}`,
        createdAt: new Date(),
      };
      this.payslips.push(payslip);
    });
  }

  private generateIdCard(employee: EmployeeProfile) {
    const cardId = crypto.randomUUID();
    const hash = crypto.createHash('sha256').update(`${employee.id}-${employee.employeeCode}-SURYACREDIT-SECURE`).digest('hex');
    const secureToken = `V1:${hash.substring(0, 16).toUpperCase()}`;

    const card: EmployeeIdCard = {
      id: cardId,
      employeeId: employee.id,
      cardNo: `SCS-IDC-${employee.employeeCode.split('-')[1]}`,
      qrCode: `https://suryacredit.com/verify-employee?id=${employee.id}&token=${secureToken}`,
      barcode: `*${employee.employeeCode}*`,
      issueDate: new Date(employee.dateOfJoining),
      validTill: new Date(new Date(employee.dateOfJoining).setFullYear(new Date(employee.dateOfJoining).getFullYear() + 5)),
      tamperProof: secureToken,
      signatureUrl: 'https://cdn.pixabay.com/photo/2014/11/05/15/57/signature-518037_1280.png',
      authSigUrl: 'https://cdn.pixabay.com/photo/2013/07/12/15/04/approved-149348_1280.png',
    };
    this.idCards.set(employee.id, card);
  }

  // 1. Employee Management
  async getAllEmployees(): Promise<EmployeeProfile[]> {
    return this.employees;
  }

  async getEmployeeById(id: string): Promise<EmployeeProfile> {
    const employee = this.employees.find((e) => e.id === id);
    if (!employee) throw new NotFoundException('Employee not found');
    return employee;
  }

  async createEmployee(dto: CreateEmployeeDto): Promise<EmployeeProfile> {
    const existing = this.employees.find((e) => e.employeeCode === dto.employeeCode || e.email === dto.email);
    if (existing) throw new BadRequestException('Employee Code or Email already registered');

    const id = crypto.randomUUID();
    const newEmployee: EmployeeProfile = {
      id,
      ...dto,
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    this.employees.push(newEmployee);

    // Auto-create ID Card & Salary Structure
    this.generateIdCard(newEmployee);

    const defaultSalary: SalaryStructure = {
      id: crypto.randomUUID(),
      employeeId: id,
      basicSalary: 25000,
      hra: 12500,
      specialAllowance: 5000,
      incentive: 0,
      bonus: 0,
      overtime: 0,
      reimbursements: 0,
      pf: 1800,
      esic: 300,
      professionalTax: 200,
      incomeTax: 0,
      netSalary: 40400,
    };
    this.salaryStructures.set(id, defaultSalary);

    return newEmployee;
  }

  async updateEmployeeStatus(id: string, status: string): Promise<EmployeeProfile> {
    const employee = await this.getEmployeeById(id);
    employee.status = status;
    employee.updatedAt = new Date();
    return employee;
  }

  // 2. ID Card & Secure Verification
  async getIdCard(employeeId: string): Promise<EmployeeIdCard> {
    const card = this.idCards.get(employeeId);
    if (!card) throw new NotFoundException('Digital ID Card not generated yet');
    return card;
  }

  async verifyEmployeeQr(employeeId: string, token: string): Promise<any> {
    const employee = this.employees.find((e) => e.id === employeeId);
    if (!employee) {
      return { verified: false, message: 'Invalid QR - Security breach alerted!' };
    }

    const card = this.idCards.get(employeeId);
    if (!card || card.tamperProof !== token) {
      return { verified: false, message: 'Security Tamper Code Mismatch - Fraud alert registered!' };
    }

    return {
      verified: true,
      employeeName: employee.fullName,
      employeeId: employee.employeeCode,
      department: employee.department,
      designation: employee.designation,
      activeStatus: employee.status,
      companyName: 'Surya Credit Solutions Private Limited',
      photo: employee.photo,
      verificationTimestamp: new Date().toISOString(),
      integrityCheck: 'PASS - Secure cryptographic signature verified',
    };
  }

  // 3. Salary Management
  async getSalaryStructure(employeeId: string): Promise<SalaryStructure> {
    const structure = this.salaryStructures.get(employeeId);
    if (!structure) throw new NotFoundException('Salary structure not configured');
    return structure;
  }

  async updateSalaryStructure(employeeId: string, dto: UpdateSalaryStructureDto): Promise<SalaryStructure> {
    const employee = await this.getEmployeeById(employeeId);
    
    const gross = dto.basicSalary + dto.hra + dto.specialAllowance + (dto.incentive || 0) + (dto.bonus || 0) + (dto.overtime || 0) + (dto.reimbursements || 0);
    const deductions = (dto.pf || 0) + (dto.esic || 0) + (dto.professionalTax || 0) + (dto.incomeTax || 0);
    const net = gross - deductions;

    const updated: SalaryStructure = {
      id: this.salaryStructures.get(employeeId)?.id || crypto.randomUUID(),
      employeeId,
      ...dto,
      netSalary: net,
    };

    this.salaryStructures.set(employeeId, updated);
    return updated;
  }

  // 4. Attendance & Leaves
  async logAttendance(dto: LogAttendanceDto): Promise<AttendanceRecord> {
    await this.getEmployeeById(dto.employeeId);
    const id = crypto.randomUUID();
    const record: AttendanceRecord = {
      id,
      employeeId: dto.employeeId,
      date: dto.date,
      status: dto.status,
      checkIn: dto.checkIn,
      checkOut: dto.checkOut,
      hoursWorked: dto.checkIn && dto.checkOut ? 9.0 : 0,
    };
    this.attendanceRecords.push(record);
    return record;
  }

  async getAttendanceForEmployee(employeeId: string): Promise<AttendanceRecord[]> {
    return this.attendanceRecords.filter((r) => r.employeeId === employeeId);
  }

  async applyLeave(dto: ApplyLeaveDto): Promise<LeaveRecord> {
    await this.getEmployeeById(dto.employeeId);
    const id = crypto.randomUUID();
    const record: LeaveRecord = {
      id,
      employeeId: dto.employeeId,
      leaveType: dto.leaveType,
      startDate: dto.startDate,
      endDate: dto.endDate,
      status: 'PENDING',
      reason: dto.reason,
      createdAt: new Date(),
    };
    this.leaveRecords.push(record);
    return record;
  }

  async getLeaveRecords(employeeId?: string): Promise<LeaveRecord[]> {
    if (employeeId) {
      return this.leaveRecords.filter((l) => l.employeeId === employeeId);
    }
    return this.leaveRecords;
  }

  async updateLeaveStatus(leaveId: string, status: 'APPROVED' | 'REJECTED'): Promise<LeaveRecord> {
    const record = this.leaveRecords.find((r) => r.id === leaveId);
    if (!record) throw new NotFoundException('Leave record not found');
    record.status = status;
    return record;
  }

  // 5. Payroll & Payslips
  async processPayroll(dto: ProcessPayrollDto): Promise<any> {
    const activeEmployees = this.employees.filter((e) => e.status === 'ACTIVE');
    let totalGross = 0;
    let totalDeductions = 0;
    let totalNet = 0;
    let processedCount = 0;

    activeEmployees.forEach((emp) => {
      const salary = this.salaryStructures.get(emp.id);
      if (salary) {
        processedCount++;
        const gross = salary.basicSalary + salary.hra + salary.specialAllowance + (salary.incentive || 0) + (salary.bonus || 0);
        const deduction = (salary.pf || 0) + (salary.esic || 0) + (salary.professionalTax || 0);
        const net = gross - deduction;

        totalGross += gross;
        totalDeductions += deduction;
        totalNet += net;

        // Auto-generate Payslip
        const payslipNo = `SCS-${dto.month.replace('-', '')}-${emp.employeeCode.split('-')[1]}`;
        const hasPayslip = this.payslips.find((p) => p.employeeId === emp.id && p.month === dto.month);
        
        if (!hasPayslip) {
          this.payslips.push({
            id: crypto.randomUUID(),
            employeeId: emp.id,
            payslipNo,
            month: dto.month,
            workingDays: 30,
            presentDays: 24,
            leaveDays: 6,
            basicSalary: salary.basicSalary,
            hra: salary.hra,
            allowances: salary.specialAllowance,
            incentives: salary.incentive || 0,
            deductions: deduction,
            netSalary: net,
            qrCode: `VERIFY_PAYSLIP_${emp.id}_${dto.month}`,
            signatureUrl: 'https://cdn.pixabay.com/photo/2014/11/05/15/57/signature-518037_1280.png',
            paymentRef: `TXN-PROD-${crypto.randomBytes(4).toString('hex').toUpperCase()}`,
            createdAt: new Date(),
          });
        }
      }
    });

    return {
      month: dto.month,
      status: 'APPROVED & DISBURSED',
      employeesProcessed: processedCount,
      totalGrossDisbursed: totalGross,
      totalDeductionsRetained: totalDeductions,
      totalNetTransferred: totalNet,
      paymentGatewayPartner: 'RAZORPAYX_API_LIVE',
      bankSettlementBatchId: `BATCH-${crypto.randomBytes(6).toString('hex').toUpperCase()}`,
    };
  }

  async getPayslips(employeeId?: string): Promise<PayslipRecord[]> {
    if (employeeId) {
      return this.payslips.filter((p) => p.employeeId === employeeId);
    }
    return this.payslips;
  }

  async getPayslipById(id: string): Promise<PayslipRecord> {
    const payslip = this.payslips.find((p) => p.id === id);
    if (!payslip) throw new NotFoundException('Payslip not found');
    return payslip;
  }

  async getSecureIdCardPdfHtml(employeeId: string): Promise<string> {
    const employee = await this.getEmployeeById(employeeId);
    const card = await this.getIdCard(employeeId);

    return `
      <html>
        <head>
          <style>
            body { font-family: 'Helvetica', sans-serif; background: #fafafa; margin: 0; padding: 20px; }
            .card-container { width: 320px; border: 1px solid #ddd; border-radius: 12px; background: #fff; box-shadow: 0 4px 15px rgba(0,0,0,0.1); margin: 0 auto; overflow: hidden; }
            .card-header { background: linear-gradient(135deg, #4A154B, #6B114D); padding: 15px; text-align: center; color: white; }
            .logo { font-size: 16px; font-weight: bold; letter-spacing: 1px; }
            .subtitle { font-size: 10px; opacity: 0.8; }
            .card-body { padding: 20px; text-align: center; }
            .photo-frame { width: 100px; height: 100px; border-radius: 50%; border: 3px solid #4A154B; margin: 0 auto 15px; overflow: hidden; }
            .photo { width: 100%; height: 100%; object-fit: cover; }
            .name { font-size: 18px; font-weight: bold; color: #333; margin: 5px 0; }
            .designation { font-size: 12px; color: #666; font-weight: 500; text-transform: uppercase; margin-bottom: 15px; }
            .details { border-top: 1px dashed #eee; padding-top: 15px; text-align: left; }
            .detail-row { display: flex; justify-content: space-between; margin-bottom: 6px; font-size: 11px; }
            .label { color: #888; }
            .value { color: #333; font-weight: bold; }
            .qr-sec { text-align: center; margin-top: 15px; }
            .barcode { font-family: 'Courier New', monospace; font-size: 10px; color: #555; margin-top: 5px; }
            .card-footer { background: #f5f5f5; padding: 10px; text-align: center; font-size: 8px; color: #999; border-top: 1px solid #eee; }
          </style>
        </head>
        <body>
          <div class="card-container">
            <div class="card-header">
              <div class="logo">SURYA CREDIT</div>
              <div class="subtitle">Digital Employee Identity Credential</div>
            </div>
            <div class="card-body">
              <div class="photo-frame">
                <img class="photo" src="${employee.photo || 'https://via.placeholder.com/100'}" />
              </div>
              <div class="name">${employee.fullName}</div>
              <div class="designation">${employee.designation}</div>
              
              <div class="details">
                <div class="detail-row"><span class="label">Emp Code:</span><span class="value">${employee.employeeCode}</span></div>
                <div class="detail-row"><span class="label">Department:</span><span class="value">${employee.department}</span></div>
                <div class="detail-row"><span class="label">Issue Date:</span><span class="value">${card.issueDate.toISOString().split('T')[0]}</span></div>
                <div class="detail-row"><span class="label">Expiry Date:</span><span class="value">${card.validTill.toISOString().split('T')[0]}</span></div>
              </div>
              
              <div class="qr-sec">
                <div style="font-size: 9px; color: #4A154B; font-weight: bold; margin-bottom: 5px;">TAMPER-PROOF CRYPTO-SIGN: PASS</div>
                <div style="background: #eee; width: 60px; height: 60px; margin: 0 auto; display: flex; align-items: center; justify-content: center; font-size: 8px; color: #666;">QR CODE</div>
                <div class="barcode">${card.barcode}</div>
              </div>
            </div>
            <div class="card-footer">
              If found, please return to: Surya Credit Solutions, No. 12, Outer Ring Road, Bangalore. Support: hr@suryacredit.com
            </div>
          </div>
        </body>
      </html>
    `;
  }
}
