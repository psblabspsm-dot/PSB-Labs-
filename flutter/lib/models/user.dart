// Multi-tenant participant roles in the Surya B2B FinTech Network.
enum SuryaRole {
  superDistributor,
  distributor,
  retailer,
  employee,
  admin,
  customer
}

extension SuryaRoleExtension on SuryaRole {
  String get label {
    switch (this) {
      case SuryaRole.superDistributor:
        return 'Super Distributor';
      case SuryaRole.distributor:
        return 'Distributor';
      case SuryaRole.retailer:
        return 'Retailer Kiosk';
      case SuryaRole.employee:
        return 'Support Executive';
      case SuryaRole.admin:
        return 'System Admin';
      case SuryaRole.customer:
        return 'End Customer';
    }
  }
}

class User {
  final String id;
  final String email;
  final String phoneNumber;
  final String fullName;
  final SuryaRole role;
  final bool isActive;
  final String? parentId; // Maps Retailer -> Distributor, or Distributor -> Super Distributor

  User({
    required this.id,
    required this.email,
    required this.phoneNumber,
    required this.fullName,
    required this.role,
    this.isActive = true,
    this.parentId,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] as String,
      email: json['email'] as String,
      phoneNumber: json['phoneNumber'] as String,
      fullName: json['fullName'] as String,
      role: _parseRole(json['role'] as String),
      isActive: json['isActive'] as bool? ?? true,
      parentId: json['parentId'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'phoneNumber': phoneNumber,
      'fullName': fullName,
      'role': role.toString().split('.').last.toUpperCase(),
      'isActive': isActive,
      'parentId': parentId,
    };
  }

  static SuryaRole _parseRole(String roleStr) {
    switch (roleStr.toUpperCase()) {
      case 'SUPER_DISTRIBUTOR':
        return SuryaRole.superDistributor;
      case 'DISTRIBUTOR':
        return SuryaRole.distributor;
      case 'EMPLOYEE':
        return SuryaRole.employee;
      case 'ADMIN':
        return SuryaRole.admin;
      case 'CUSTOMER':
        return SuryaRole.customer;
      case 'RETAILER':
      default:
        return SuryaRole.retailer;
    }
  }
}
