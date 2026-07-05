import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../models/marketplace.dart';

class B2BMarketplaceProvider with ChangeNotifier {
  final String _baseUrl = 'https://ais-dev-vpta6fjojwyfqdgrlle3uu-251346388939.asia-east1.run.app/api/v1/marketplace';
  
  List<B2BProduct> _products = [];
  List<B2BOrder> _orders = [];
  List<B2BWarehouse> _warehouses = [];
  final Map<B2BProduct, int> _cart = {};
  bool _isLoading = false;
  String _errorMessage = '';

  List<B2BProduct> get products => _products;
  List<B2BOrder> get orders => _orders;
  List<B2BWarehouse> get warehouses => _warehouses;
  Map<B2BProduct, int> get cart => _cart;
  bool get isLoading => _isLoading;
  String get errorMessage => _errorMessage;

  int get cartCount => _cart.values.fold(0, (sum, count) => sum + count);

  double get cartSubtotal => _cart.entries.fold(0.0, (sum, entry) => sum + (entry.key.price * entry.value));

  double get cartGstTotal => _cart.entries.fold(0.0, (sum, entry) => sum + (entry.key.price * entry.value * (entry.key.gstRate / 100)));

  double get cartTotalWithGst => cartSubtotal + cartGstTotal;

  Future<void> fetchProducts() async {
    _isLoading = true;
    _errorMessage = '';
    notifyListeners();

    try {
      final response = await http.get(Uri.parse('$_baseUrl/products'));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        _products = data.map((json) => B2BProduct.fromJson(json)).toList();
      } else {
        _errorMessage = 'Failed to load catalogue products';
      }
    } catch (e) {
      _errorMessage = 'Connection failure: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchWarehouses() async {
    try {
      final response = await http.get(Uri.parse('$_baseUrl/warehouses'));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        _warehouses = data.map((json) => B2BWarehouse.fromJson(json)).toList();
        notifyListeners();
      }
    } catch (_) {}
  }

  void addToCart(B2BProduct product) {
    if (_cart.containsKey(product)) {
      _cart[product] = _cart[product]! + 1;
    } else {
      _cart[product] = 1;
    }
    notifyListeners();
  }

  void removeFromCart(B2BProduct product) {
    if (!_cart.containsKey(product)) return;
    if (_cart[product] == 1) {
      _cart.remove(product);
    } else {
      _cart[product] = _cart[product]! - 1;
    }
    notifyListeners();
  }

  void clearCart() {
    _cart.clear();
    notifyListeners();
  }

  Future<bool> checkoutCart(String userId, String paymentMethod) async {
    if (_cart.isEmpty) return false;

    _isLoading = true;
    notifyListeners();

    final checkoutItems = _cart.entries.map((entry) => {
      'productId': entry.key.id,
      'quantity': entry.value
    }).toList();

    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/orders/checkout/$userId'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'items': checkoutItems,
          'paymentMethod': paymentMethod,
        }),
      );

      if (response.statusCode == 200) {
        _cart.clear();
        await fetchProducts(); // Refresh stocks
        _isLoading = false;
        notifyListeners();
        return true;
      }
    } catch (_) {}

    _isLoading = false;
    notifyListeners();
    return false;
  }
}
