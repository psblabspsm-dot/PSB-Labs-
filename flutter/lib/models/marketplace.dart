class B2BProduct {
  final String id;
  final String sku;
  final String name;
  final String category;
  final String brand;
  final double price;
  final int moq;
  final String description;
  final List<String> variants;
  final double gstRate;
  final String hsnCode;
  final int stock;
  final int lowStockAlertLevel;
  final String vendorId;
  final String vendorName;

  B2BProduct({
    required this.id,
    required this.sku,
    required this.name,
    required this.category,
    required this.brand,
    required this.price,
    required this.moq,
    required this.description,
    required this.variants,
    required this.gstRate,
    required this.hsnCode,
    required this.stock,
    required this.lowStockAlertLevel,
    required this.vendorId,
    required this.vendorName,
  });

  factory B2BProduct.fromJson(Map<String, dynamic> json) {
    return B2BProduct(
      id: json['id'] ?? '',
      sku: json['sku'] ?? '',
      name: json['name'] ?? '',
      category: json['category'] ?? '',
      brand: json['brand'] ?? '',
      price: (json['price'] ?? 0.0).toDouble(),
      moq: json['moq'] ?? 1,
      description: json['description'] ?? '',
      variants: List<String>.from(json['variants'] ?? []),
      gstRate: (json['gstRate'] ?? 18.0).toDouble(),
      hsnCode: json['hsnCode'] ?? '',
      stock: json['stock'] ?? 0,
      lowStockAlertLevel: json['lowStockAlertLevel'] ?? 10,
      vendorId: json['vendorId'] ?? '',
      vendorName: json['vendorName'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'sku': sku,
      'name': name,
      'category': category,
      'brand': brand,
      'price': price,
      'moq': moq,
      'description': description,
      'variants': variants,
      'gstRate': gstRate,
      'hsnCode': hsnCode,
      'stock': stock,
      'lowStockAlertLevel': lowStockAlertLevel,
      'vendorId': vendorId,
      'vendorName': vendorName,
    };
  }
}

class B2BOrder {
  final String id;
  final String userId;
  final String productNames;
  final List<B2BOrderItem> items;
  final double totalAmount;
  final double gstTotal;
  final double finalWithGst;
  final String paymentMethod;
  final String? paymentGateway;
  final String status; // PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURN_REQUESTED, RETURNED
  final String? shipmentTracker;
  final String? shippingPartner;
  final String? warehouseAssigned;
  final DateTime createdAt;
  final DateTime updatedAt;

  B2BOrder({
    required this.id,
    required this.userId,
    required this.productNames,
    required this.items,
    required this.totalAmount,
    required this.gstTotal,
    required this.finalWithGst,
    required this.paymentMethod,
    this.paymentGateway,
    required this.status,
    this.shipmentTracker,
    this.shippingPartner,
    this.warehouseAssigned,
    required this.createdAt,
    required this.updatedAt,
  });

  factory B2BOrder.fromJson(Map<String, dynamic> json) {
    return B2BOrder(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      productNames: json['productNames'] ?? '',
      items: (json['items'] as List? ?? [])
          .map((item) => B2BOrderItem.fromJson(item))
          .toList(),
      totalAmount: (json['totalAmount'] ?? 0.0).toDouble(),
      gstTotal: (json['gstTotal'] ?? 0.0).toDouble(),
      finalWithGst: (json['finalWithGst'] ?? 0.0).toDouble(),
      paymentMethod: json['paymentMethod'] ?? '',
      paymentGateway: json['paymentGateway'],
      status: json['status'] ?? 'PENDING',
      shipmentTracker: json['shipmentTracker'],
      shippingPartner: json['shippingPartner'],
      warehouseAssigned: json['warehouseAssigned'],
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : DateTime.now(),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : DateTime.now(),
    );
  }
}

class B2BOrderItem {
  final String productId;
  final String name;
  final int quantity;
  final double price;
  final double gstAmount;
  final double total;

  B2BOrderItem({
    required this.productId,
    required this.name,
    required this.quantity,
    required this.price,
    required this.gstAmount,
    required this.total,
  });

  factory B2BOrderItem.fromJson(Map<String, dynamic> json) {
    return B2BOrderItem(
      productId: json['productId'] ?? '',
      name: json['name'] ?? '',
      quantity: json['quantity'] ?? 0,
      price: (json['price'] ?? 0.0).toDouble(),
      gstAmount: (json['gstAmount'] ?? 0.0).toDouble(),
      total: (json['total'] ?? 0.0).toDouble(),
    );
  }
}

class B2BWarehouse {
  final String id;
  final String name;
  final String location;
  final int capacity;
  final int availableStockCount;

  B2BWarehouse({
    required this.id,
    required this.name,
    required this.location,
    required this.capacity,
    required this.availableStockCount,
  });

  factory B2BWarehouse.fromJson(Map<String, dynamic> json) {
    return B2BWarehouse(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      location: json['location'] ?? '',
      capacity: json['capacity'] ?? 0,
      availableStockCount: json['availableStockCount'] ?? 0,
    );
  }
}
