import { Injectable, BadRequestException, NotFoundException, Logger } from '@nestjs/common';
import * as crypto from 'crypto';

export interface B2BProduct {
  id: string;
  sku: string;
  name: string;
  category: string;
  brand: string;
  price: number;
  moq: number; // Minimum Order Quantity
  description: string;
  variants: string[];
  gstRate: number; // GST slab percentage, e.g. 18
  hsnCode: string;
  stock: number;
  lowStockAlertLevel: number;
  vendorId: string;
  vendorName: string;
}

export interface B2BOrder {
  id: string;
  userId: string;
  productNames: string;
  items: {
    productId: string;
    name: string;
    quantity: number;
    price: number;
    gstAmount: number;
    total: number;
  }[];
  totalAmount: number;
  gstTotal: number;
  finalWithGst: number;
  paymentMethod: string;
  paymentGateway?: string;
  status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'RETURN_REQUESTED' | 'RETURNED';
  shipmentTracker?: string;
  shippingPartner?: string;
  warehouseAssigned?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface B2BVendor {
  id: string;
  companyName: string;
  ownerName: string;
  pan: string;
  gstin: string;
  kycStatus: 'NOT_SUBMITTED' | 'SUBMITTED' | 'APPROVED' | 'REJECTED';
  rating: number;
  walletBalance: number;
}

export interface B2BWarehouse {
  id: string;
  name: string;
  location: string;
  capacity: number;
  availableStockCount: number;
}

@Injectable()
export class MarketplaceService {
  private readonly logger = new Logger('SuryaMarketplaceCore');

  // Hardcoded product catalog representing advanced smart payment & telecom terminal hardware
  private products: B2BProduct[] = [
    {
      id: 'prod-01',
      sku: 'SKU-DMT-PAX90',
      name: 'Surya Smart POS Terminal Pax A90',
      category: 'POS Devices',
      brand: 'Surya / Pax',
      price: 14500.0,
      moq: 5,
      description: 'Rugged enterprise smart android POS terminal with internal high-speed printer and hybrid chip reader.',
      variants: ['Sleek Metallic Silver', 'Titanium Black Core', 'Active Gold edition'],
      gstRate: 18,
      hsnCode: '84713010',
      stock: 850,
      lowStockAlertLevel: 50,
      vendorId: 'vnd-01',
      vendorName: 'Surya Hardware OEM Manufacturing Division'
    },
    {
      id: 'prod-02',
      sku: 'SKU-BIO-MANTRA',
      name: 'Mantra MFS100 Biometric Scanner',
      category: 'Biometric',
      brand: 'Mantra',
      price: 2400.0,
      moq: 10,
      description: 'UIDAI-registered biometric fingerprint scanner for AEPS cash withdrawal and e-KYC compliance.',
      variants: ['USB Classic grey', 'Type-C Slate Blue'],
      gstRate: 12,
      hsnCode: '84716050',
      stock: 1200,
      lowStockAlertLevel: 100,
      vendorId: 'vnd-02',
      vendorName: 'Bangalore Biometrics Distributors Pvt Ltd'
    },
    {
      id: 'prod-03',
      sku: 'SKU-PRN-THRM58',
      name: 'Surya High-Speed Thermal Printer',
      category: 'Thermal Printers',
      brand: 'Surya',
      price: 3200.0,
      moq: 8,
      description: '58mm high-durability Bluetooth-enabled thermal printer with automatic continuous receipt cutting.',
      variants: ['Compact Black', 'Ultra-Portable Slate Grey'],
      gstRate: 18,
      hsnCode: '84433210',
      stock: 450,
      lowStockAlertLevel: 30,
      vendorId: 'vnd-01',
      vendorName: 'Surya Hardware OEM Manufacturing Division'
    },
    {
      id: 'prod-04',
      sku: 'SKU-MATM-D200',
      name: 'Surya Smart mATM D200 Terminal',
      category: 'POS Devices',
      brand: 'Surya',
      price: 1800.0,
      moq: 15,
      description: 'Pocket-sized micro-ATM terminal with EMV card readers, compliant with RBI NPCI security.',
      variants: ['Standard Navy Blue'],
      gstRate: 18,
      hsnCode: '84719000',
      stock: 2200,
      lowStockAlertLevel: 150,
      vendorId: 'vnd-01',
      vendorName: 'Surya Hardware OEM Manufacturing Division'
    }
  ];

  // Vendors registry memory store
  private vendors: Record<string, B2BVendor> = {
    'vnd-01': {
      id: 'vnd-01',
      companyName: 'Surya Hardware OEM Manufacturing Division',
      ownerName: 'Surya Executive Board',
      pan: 'AABCS9028A',
      gstin: '29AABCS9028A1ZH',
      kycStatus: 'APPROVED',
      rating: 4.9,
      walletBalance: 2450000.0,
    },
    'vnd-02': {
      id: 'vnd-02',
      companyName: 'Bangalore Biometrics Distributors Pvt Ltd',
      ownerName: 'Kishore Kumar',
      pan: 'AAACK1102K',
      gstin: '29AAACK1102K1Z9',
      kycStatus: 'APPROVED',
      rating: 4.5,
      walletBalance: 120000.0,
    }
  };

  // Warehouses network
  private warehouses: B2BWarehouse[] = [
    { id: 'wh-01', name: 'Bangalore Nodal Logistics Warehouse', location: 'Whitefield Industrial Zone', capacity: 10000, availableStockCount: 4700 },
    { id: 'wh-02', name: 'Mumbai Airport Regional Transit Depot', location: 'Andheri Logistics hub', capacity: 5000, availableStockCount: 2200 },
    { id: 'wh-03', name: 'Delhi NCR Fulfillment Center', location: 'Gurugram Sector-84', capacity: 8000, availableStockCount: 3100 }
  ];

  // B2B orders records
  private orders: B2BOrder[] = [
    {
      id: 'ORD-2026-9021',
      userId: 'usr-ret-03',
      productNames: 'Surya Smart POS Terminal Pax A90',
      items: [
        {
          productId: 'prod-01',
          name: 'Surya Smart POS Terminal Pax A90',
          quantity: 5,
          price: 14500.0,
          gstAmount: 13050.0, // 18% on total 72500
          total: 72500.0
        }
      ],
      totalAmount: 72500.0,
      gstTotal: 13050.0,
      finalWithGst: 85550.0,
      paymentMethod: 'WALLET',
      paymentGateway: undefined,
      status: 'DELIVERED',
      shipmentTracker: 'BLUEDART-BD7829103',
      shippingPartner: 'BlueDart Air',
      warehouseAssigned: 'Bangalore Nodal Logistics Warehouse',
      createdAt: new Date('2026-06-25T11:00:00Z'),
      updatedAt: new Date('2026-06-28T16:20:00Z')
    }
  ];

  // Support tickets / inquiries
  private supportTickets: any[] = [];

  getProducts(): B2BProduct[] {
    return this.products;
  }

  getProductById(id: string): B2BProduct {
    const product = this.products.find(p => p.id === id);
    if (!product) throw new NotFoundException('Requested product SKU not found in our catalog.');
    return product;
  }

  // Create/inject a custom merchant OEM variant (multi-vendor product creation)
  createProduct(data: Partial<B2BProduct>) {
    const id = 'prod-' + Math.floor(10000 + Math.random() * 90000);
    const sku = data.sku || 'SKU-' + Math.floor(100000 + Math.random() * 900000);
    const newProduct: B2BProduct = {
      id,
      sku,
      name: data.name || 'Custom Hardware Component',
      category: data.category || 'POS Devices',
      brand: data.brand || 'Generic OEM',
      price: data.price || 5000.0,
      moq: data.moq || 5,
      description: data.description || 'Highly efficient telecom POS e-commerce terminal device component.',
      variants: data.variants || ['Standard Gray Slate'],
      gstRate: data.gstRate || 18,
      hsnCode: data.hsnCode || '84719000',
      stock: data.stock || 100,
      lowStockAlertLevel: data.lowStockAlertLevel || 10,
      vendorId: data.vendorId || 'vnd-03',
      vendorName: data.vendorName || 'Independent OEM Merchant Partner'
    };

    this.products.unshift(newProduct);
    this.logger.log(`[MARKETPLACE] New product registered. SKU: ${sku} Name: ${newProduct.name}`);
    return newProduct;
  }

  // Multi-vendor onboarding and business KYC compliance filing
  registerVendor(userId: string, data: { companyName: string; ownerName: string; pan: string; gstin: string }) {
    const vendorId = 'vnd-' + Math.floor(1000 + Math.random() * 9000);
    const newVendor: B2BVendor = {
      id: vendorId,
      companyName: data.companyName,
      ownerName: data.ownerName,
      pan: data.pan,
      gstin: data.gstin,
      kycStatus: 'APPROVED', // Auto approved for development
      rating: 5.0,
      walletBalance: 0.0
    };

    this.vendors[userId] = newVendor;
    this.logger.log(`[VENDOR_KYC] Onboarded new merchant manufacturer partner: ${data.companyName}`);
    return {
      message: 'Vendor Business Onboarding Complete and Approved.',
      vendor: newVendor
    };
  }

  getVendor(userId: string): B2BVendor | null {
    return this.vendors[userId] || null;
  }

  getOrders(userId?: string): B2BOrder[] {
    if (userId) {
      return this.orders.filter(o => o.userId === userId);
    }
    return this.orders;
  }

  // Enterprise Procurement Order Compilation & Checkout
  checkoutCart(userId: string, items: { productId: string; quantity: number }[], paymentMethod: string, gateway?: string) {
    if (items.length === 0) {
      throw new BadRequestException('B2B procurement basket is empty.');
    }

    let totalAmount = 0;
    let gstTotal = 0;
    const orderItems: any[] = [];
    const productNamesList: string[] = [];

    for (const item of items) {
      const prod = this.getProductById(item.productId);
      if (prod.stock < item.quantity) {
        throw new BadRequestException(`Insufficient factory inventory for SKU: ${prod.sku}. Only ${prod.stock} left.`);
      }

      const itemTotal = prod.price * item.quantity;
      const itemGst = itemTotal * (prod.gstRate / 100);

      totalAmount += itemTotal;
      gstTotal += itemGst;

      prod.stock -= item.quantity; // Deduct warehouse inventory allocation

      orderItems.push({
        productId: prod.id,
        name: prod.name,
        quantity: item.quantity,
        price: prod.price,
        gstAmount: itemGst,
        total: itemTotal
      });

      productNamesList.push(`${prod.name} (x${item.quantity})`);
    }

    const finalWithGst = totalAmount + gstTotal;
    const orderId = 'ORD-2026-' + Math.floor(10000 + Math.random() * 90000);

    const warehouse = this.warehouses[Math.floor(Math.random() * this.warehouses.length)];
    const shipping = 'DELIVERY-' + Math.floor(100000 + Math.random() * 900000);

    const newOrder: B2BOrder = {
      id: orderId,
      userId,
      productNames: productNamesList.join(', '),
      items: orderItems,
      totalAmount,
      gstTotal,
      finalWithGst,
      paymentMethod,
      paymentGateway: gateway,
      status: 'CONFIRMED',
      shipmentTracker: shipping,
      shippingPartner: 'Surya SuperExpress Fleet BlueDart',
      warehouseAssigned: warehouse.name,
      createdAt: new Date(),
      updatedAt: new Date()
    };

    this.orders.unshift(newOrder);
    this.logger.log(`[ORDER] Placed B2B Order: ${orderId} | Total: ₹${finalWithGst}`);

    return {
      message: 'B2B order generated under GST billing parameters.',
      orderId,
      totalWithGst: finalWithGst,
      assignedLogistics: {
        warehouse: warehouse.name,
        tracker: shipping,
        partner: 'Surya SuperExpress Fleet BlueDart'
      },
      order: newOrder
    };
  }

  // Handle returns/cancellations
  triggerOrderReturn(orderId: string, reason: string) {
    const order = this.orders.find(o => o.id === orderId);
    if (!order) throw new NotFoundException('Order not found.');

    order.status = 'RETURN_REQUESTED';
    order.updatedAt = new Date();

    const ticketId = 'TCK-' + Math.floor(10000 + Math.random() * 90000);
    this.supportTickets.unshift({
      id: ticketId,
      orderId,
      reason,
      status: 'OPEN',
      createdAt: new Date()
    });

    return {
      message: 'Return request submitted. Support ticket dispatched.',
      orderStatus: order.status,
      ticketId
    };
  }

  getWarehouses(): B2BWarehouse[] {
    return this.warehouses;
  }
}
