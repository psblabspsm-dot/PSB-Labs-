class FintechTransaction {
  final String id;
  final String userId;
  final String serviceType; // RECHARGE, BBPS, AEPS, DMT, CREDIT_PAY, TRAVEL, INSURANCE, PAN
  final double amount;
  final String description;
  final String status; // SUCCESS, PENDING, FAILED
  final String referenceId;
  final String paymentMethod; // WALLET, CREDIT_LINE
  final double cgst;
  final double sgst;
  final double commissionAmt;
  final DateTime createdAt;

  FintechTransaction({
    required this.id,
    required this.userId,
    required this.serviceType,
    required this.amount,
    required this.description,
    required this.status,
    required this.referenceId,
    required this.paymentMethod,
    required this.cgst,
    required this.sgst,
    required this.commissionAmt,
    required this.createdAt,
  });

  factory FintechTransaction.fromJson(Map<String, dynamic> json) {
    return FintechTransaction(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      serviceType: json['serviceType'] ?? '',
      amount: (json['amount'] ?? 0.0).toDouble(),
      description: json['description'] ?? '',
      status: json['status'] ?? 'PENDING',
      referenceId: json['referenceId'] ?? '',
      paymentMethod: json['paymentMethod'] ?? 'WALLET',
      cgst: (json['cgst'] ?? 0.0).toDouble(),
      sgst: (json['sgst'] ?? 0.0).toDouble(),
      commissionAmt: (json['commissionAmt'] ?? 0.0).toDouble(),
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'serviceType': serviceType,
      'amount': amount,
      'description': description,
      'status': status,
      'referenceId': referenceId,
      'paymentMethod': paymentMethod,
      'cgst': cgst,
      'sgst': sgst,
      'commissionAmt': commissionAmt,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

class PanApplication {
  final String id;
  final String applicantName;
  final String applicationType; // NEW, CORRECTION, REPRINT
  final String aadhaarNo;
  final String contactNo;
  final double fee;
  final String status; // PENDING, PROCESSING, SUCCESS, FAILED
  final DateTime createdAt;

  PanApplication({
    required this.id,
    required this.applicantName,
    required this.applicationType,
    required this.aadhaarNo,
    required this.contactNo,
    required this.fee,
    required this.status,
    required this.createdAt,
  });

  factory PanApplication.fromJson(Map<String, dynamic> json) {
    return PanApplication(
      id: json['id'] ?? '',
      applicantName: json['applicantName'] ?? '',
      applicationType: json['applicationType'] ?? 'NEW',
      aadhaarNo: json['aadhaarNo'] ?? '',
      contactNo: json['contactNo'] ?? '',
      fee: (json['fee'] ?? 107.0).toDouble(),
      status: json['status'] ?? 'PENDING',
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : DateTime.now(),
    );
  }
}

class TravelBooking {
  final String id;
  final String category; // FLIGHT, BUS, HOTEL
  final String source;
  final String destination;
  final DateTime travelDate;
  final String travellers;
  final double cost;
  final String status; // CONFIRMED, CANCELLED
  final DateTime createdAt;

  TravelBooking({
    required this.id,
    required this.category,
    required this.source,
    required this.destination,
    required this.travelDate,
    required this.travellers,
    required this.cost,
    required this.status,
    required this.createdAt,
  });

  factory TravelBooking.fromJson(Map<String, dynamic> json) {
    return TravelBooking(
      id: json['id'] ?? '',
      category: json['category'] ?? 'FLIGHT',
      source: json['source'] ?? '',
      destination: json['destination'] ?? '',
      travelDate: json['travelDate'] != null ? DateTime.parse(json['travelDate']) : DateTime.now(),
      travellers: json['travellers'] ?? '1 Traveller',
      cost: (json['cost'] ?? 0.0).toDouble(),
      status: json['status'] ?? 'PENDING',
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : DateTime.now(),
    );
  }
}

class InsurancePolicy {
  final String id;
  final String category; // HEALTH, LIFE, MOTOR, SHOP
  final String sumAssured;
  final String details;
  final double premium;
  final String status; // ACTIVE, PENDING
  final DateTime createdAt;

  InsurancePolicy({
    required this.id,
    required this.category,
    required this.sumAssured,
    required this.details,
    required this.premium,
    required this.status,
    required this.createdAt,
  });

  factory InsurancePolicy.fromJson(Map<String, dynamic> json) {
    return InsurancePolicy(
      id: json['id'] ?? '',
      category: json['category'] ?? 'HEALTH',
      sumAssured: json['sumAssured'] ?? '',
      details: json['details'] ?? '',
      premium: (json['premium'] ?? 0.0).toDouble(),
      status: json['status'] ?? 'PENDING',
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : DateTime.now(),
    );
  }
}

class ProviderConfig {
  final String serviceType;
  final String activeProvider;
  final bool isEnabled;

  ProviderConfig({
    required this.serviceType,
    required this.activeProvider,
    required this.isEnabled,
  });

  factory ProviderConfig.fromJson(Map<String, dynamic> json) {
    return ProviderConfig(
      serviceType: json['serviceType'] ?? '',
      activeProvider: json['activeProvider'] ?? '',
      isEnabled: json['isEnabled'] ?? true,
    );
  }
}
