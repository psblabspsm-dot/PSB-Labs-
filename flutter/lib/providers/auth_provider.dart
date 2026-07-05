import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/user.dart';

// Authentication State container representing modern Riverpod architecture
class AuthState {
  final AsyncValue<User?> user;
  final String? jwtToken;
  final bool isBiometricEnabled;

  AuthState({
    required this.user,
    this.jwtToken,
    this.isBiometricEnabled = false,
  });

  AuthState copyWith({
    AsyncValue<User?>? user,
    String? jwtToken,
    bool? isBiometricEnabled,
  }) {
    return AuthState(
      user: user ?? this.user,
      jwtToken: jwtToken ?? this.jwtToken,
      isBiometricEnabled: isBiometricEnabled ?? this.isBiometricEnabled,
    );
  }
}

// StateNotifier controlling multi-tenant sign-ins, security PIN handshakes and role selections
class AuthNotifier extends StateNotifier<AuthState> {
  AuthNotifier() : super(AuthState(user: const AsyncValue.data(null)));

  // Perform dynamic secure login
  Future<void> loginWithMpin(String phoneNumber, String mpin) async {
    state = state.copyWith(user: const AsyncValue.loading());

    try {
      // Secure network delay mimicking server-side NestJS encryption handshake
      await Future.delayed(const Duration(milliseconds: 1200));

      if (phoneNumber == '9876543210') {
        // Authenticate as Distributor
        final distributorUser = User(
          id: 'usr-dist-02',
          email: 'distributor@suryacredit.com',
          phoneNumber: '9876543210',
          fullName: 'Ramesh Pai Distributors',
          role: SuryaRole.distributor,
        );
        state = state.copyWith(
          user: AsyncValue.data(distributorUser),
          jwtToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.MockDistributorPayloadSig',
        );
      } else {
        // Default Retailer Kiosk
        final retailerUser = User(
          id: 'usr-ret-03',
          email: 'retailer@suryacredit.com',
          phoneNumber: phoneNumber,
          fullName: 'Surya Digital World (Proprietor)',
          role: SuryaRole.retailer,
          parentId: 'usr-dist-02',
        );
        state = state.copyWith(
          user: AsyncValue.data(retailerUser),
          jwtToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.MockRetailerPayloadSig',
        );
      }
    } catch (err, stack) {
      state = state.copyWith(
        user: AsyncValue.error(err, stack),
      );
    }
  }

  // Register a new kiosk partner with structural parent-child distributor link
  Future<void> registerKiosk({
    required String businessName,
    required String proprietorName,
    required String phoneNumber,
    required String panCard,
    required SuryaRole selectedRole,
  }) async {
    state = state.copyWith(user: const AsyncValue.loading());

    try {
      await Future.delayed(const Duration(seconds: 1));
      
      final newUser = User(
        id: 'usr-new-${DateTime.now().millisecondsSinceEpoch}',
        email: '${proprietorName.toLowerCase().replaceAll(' ', '')}@suryacredit.com',
        phoneNumber: phoneNumber,
        fullName: proprietorName,
        role: selectedRole,
        parentId: selectedRole == SuryaRole.retailer ? 'usr-dist-02' : null,
      );

      state = state.copyWith(
        user: AsyncValue.data(newUser),
        jwtToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.NewUserRegistrationSig',
      );
    } catch (err, stack) {
      state = state.copyWith(user: AsyncValue.error(err, stack));
    }
  }

  void logout() {
    state = AuthState(user: const AsyncValue.data(null));
  }
}

// Global provider for application auth states
final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier();
});
