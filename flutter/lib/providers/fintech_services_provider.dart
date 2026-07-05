import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:http/http.dart' as http;
import '../models/fintech_services.dart';

class FintechState {
  final List<FintechTransaction> transactions;
  final List<PanApplication> panApplications;
  final List<TravelBooking> travelBookings;
  final List<InsurancePolicy> insurancePolicies;
  final Map<String, ProviderConfig> configurations;
  final bool isLoading;
  final String? errorMessage;

  FintechState({
    required this.transactions,
    required this.panApplications,
    required this.travelBookings,
    required this.insurancePolicies,
    required this.configurations,
    this.isLoading = false,
    this.errorMessage,
  });

  FintechState copyWith({
    List<FintechTransaction>? transactions,
    List<PanApplication>? panApplications,
    List<TravelBooking>? travelBookings,
    List<InsurancePolicy>? insurancePolicies,
    Map<String, ProviderConfig>? configurations,
    bool? isLoading,
    String? errorMessage,
  }) {
    return FintechState(
      transactions: transactions ?? this.transactions,
      panApplications: panApplications ?? this.panApplications,
      travelBookings: travelBookings ?? this.travelBookings,
      insurancePolicies: insurancePolicies ?? this.insurancePolicies,
      configurations: configurations ?? this.configurations,
      isLoading: isLoading ?? this.isLoading,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class FintechNotifier extends StateNotifier<FintechState> {
  final String _baseUrl = 'https://ais-dev-vpta6fjojwyfqdgrlle3uu-251346388939.asia-east1.run.app/api/v1';

  FintechNotifier()
      : super(FintechState(
          transactions: [],
          panApplications: [],
          travelBookings: [],
          insurancePolicies: [],
          configurations: {
            'RECHARGE': ProviderConfig(serviceType: 'RECHARGE', activeProvider: 'Surya Telecom Hub', isEnabled: true),
            'AEPS': ProviderConfig(serviceType: 'AEPS', activeProvider: 'Yes Bank AEPS Engine v2', isEnabled: true),
            'DMT': ProviderConfig(serviceType: 'DMT', activeProvider: 'Surya Nodal IMPS Channel', isEnabled: true),
            'PAN': ProviderConfig(serviceType: 'PAN', activeProvider: 'UTI Infrastructure Technology (UTIITSL)', isEnabled: true),
            'TRAVEL': ProviderConfig(serviceType: 'TRAVEL', activeProvider: 'Surya B2B Galileo API', isEnabled: true),
            'INSURANCE': ProviderConfig(serviceType: 'INSURANCE', activeProvider: 'Surya Premium Insurance Desk', isEnabled: true),
          },
        ));

  Future<void> fetchHistory(String userId) async {
    state = state.copyWith(isLoading: true, errorMessage: null);
    try {
      final response = await http.get(Uri.parse('$_baseUrl/wallet/$userId/balance'));
      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        final List<dynamic> txsJson = data['transactions'] ?? [];
        final List<FintechTransaction> list = txsJson.map((tx) => FintechTransaction.fromJson(tx)).toList();
        state = state.copyWith(transactions: list, isLoading: false);
      } else {
        state = state.copyWith(isLoading: false, errorMessage: 'Failed to retrieve transaction log');
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, errorMessage: 'Network failure: $e');
    }
  }

  Future<bool> executeTxn({
    required String userId,
    required String service,
    required double amount,
    required String description,
    String paymentMethod = 'WALLET',
  }) async {
    state = state.copyWith(isLoading: true);
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/wallet/$userId/transaction'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'amount': amount,
          'service': service,
          'description': description,
          'paymentMethod': paymentMethod,
        }),
      );
      if (response.statusCode == 200) {
        await fetchHistory(userId);
        return true;
      }
    } catch (_) {}
    state = state.copyWith(isLoading: false);
    return false;
  }

  Future<bool> applyForPan({
    required String userId,
    required String applicantName,
    required String appType,
    required String aadhaar,
    required String contact,
  }) async {
    final success = await executeTxn(
      userId: userId,
      service: 'BBPS',
      amount: 107.0,
      description: 'PAN fee: $applicantName ($appType)',
    );

    if (success) {
      final newApp = PanApplication(
        id: 'PAN-${DateTime.now().millisecondsSinceEpoch}',
        applicantName: applicantName,
        applicationType: appType,
        aadhaarNo: aadhaar,
        contactNo: contact,
        fee: 107.0,
        status: 'SUBMITTED',
        createdAt: DateTime.now(),
      );
      state = state.copyWith(
        panApplications: [newApp, ...state.panApplications],
      );
      return true;
    }
    return false;
  }

  Future<bool> bookTravel({
    required String userId,
    required String category,
    required String source,
    required String destination,
    required DateTime date,
    required String travellers,
    required double cost,
  }) async {
    final success = await executeTxn(
      userId: userId,
      service: 'BBPS',
      amount: cost,
      description: '$category Journey: $source to $destination on ${date.toLocal()}',
    );

    if (success) {
      final booking = TravelBooking(
        id: 'TRV-${DateTime.now().millisecondsSinceEpoch}',
        category: category,
        source: source,
        destination: destination,
        travelDate: date,
        travellers: travellers,
        cost: cost,
        status: 'CONFIRMED',
        createdAt: DateTime.now(),
      );
      state = state.copyWith(
        travelBookings: [booking, ...state.travelBookings],
      );
      return true;
    }
    return false;
  }

  Future<bool> buyInsurance({
    required String userId,
    required String category,
    required String coverage,
    required String details,
    required double premium,
  }) async {
    final success = await executeTxn(
      userId: userId,
      service: 'BBPS',
      amount: premium,
      description: '$category Cover sum: $coverage',
    );

    if (success) {
      final policy = InsurancePolicy(
        id: 'INS-${DateTime.now().millisecondsSinceEpoch}',
        category: category,
        sumAssured: coverage,
        details: details,
        premium: premium,
        status: 'ACTIVE',
        createdAt: DateTime.now(),
      );
      state = state.copyWith(
        insurancePolicies: [policy, ...state.insurancePolicies],
      );
      return true;
    }
    return false;
  }

  void updateProviderConfig(String service, String provider, bool isEnabled) {
    final updatedMap = Map<String, ProviderConfig>.from(state.configurations);
    updatedMap[service] = ProviderConfig(
      serviceType: service,
      activeProvider: provider,
      isEnabled: isEnabled,
    );
    state = state.copyWith(configurations: updatedMap);
  }
}

final fintechProvider = StateNotifierProvider<FintechNotifier, FintechState>((ref) {
  return FintechNotifier();
});
