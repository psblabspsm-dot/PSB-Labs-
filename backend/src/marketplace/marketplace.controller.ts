import { Controller, Get, Post, Body, HttpCode, HttpStatus, Param, Query } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiParam, ApiBody, ApiQuery } from '@nestjs/swagger';
import { MarketplaceService, B2BProduct, B2BOrder, B2BVendor, B2BWarehouse } from './marketplace.service';
import { IsNotEmpty, IsNumber, Min, IsString, IsOptional, IsArray, ValidateNested } from 'class-validator';
import { Type } from 'class-transformer';

export class CartItemDto {
  @IsString()
  @IsNotEmpty()
  productId: string;

  @IsNumber()
  @Min(1)
  quantity: number;
}

export class CheckoutDto {
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CartItemDto)
  items: CartItemDto[];

  @IsString()
  @IsNotEmpty()
  paymentMethod: string; // WALLET, CREDIT_LINE, UPI, GATEWAY

  @IsString()
  @IsOptional()
  paymentGateway?: string;
}

export class VendorRegisterDto {
  @IsString()
  @IsNotEmpty()
  companyName: string;

  @IsString()
  @IsNotEmpty()
  ownerName: string;

  @IsString()
  @IsNotEmpty()
  pan: string;

  @IsString()
  @IsNotEmpty()
  gstin: string;
}

export class CreateProductDto {
  @IsString()
  @IsNotEmpty()
  name: string;

  @IsString()
  @IsNotEmpty()
  category: string;

  @IsString()
  @IsNotEmpty()
  brand: string;

  @IsNumber()
  @Min(1)
  price: number;

  @IsNumber()
  @Min(1)
  moq: number;

  @IsString()
  @IsNotEmpty()
  description: string;

  @IsArray()
  @IsString({ each: true })
  variants: string[];

  @IsNumber()
  @Min(0)
  gstRate: number;

  @IsString()
  @IsNotEmpty()
  hsnCode: string;

  @IsNumber()
  @Min(1)
  stock: number;

  @IsString()
  @IsNotEmpty()
  vendorId: string;

  @IsString()
  @IsNotEmpty()
  vendorName: string;
}

export class ReturnRequestDto {
  @IsString()
  @IsNotEmpty()
  reason: string;
}

@ApiTags('B2B Marketplace Engine')
@Controller('api/v1/marketplace')
export class MarketplaceController {
  constructor(private readonly marketplaceService: MarketplaceService) {}

  @Get('products')
  @ApiOperation({ summary: 'List all premium B2B terminal hardware items' })
  getProducts(): B2BProduct[] {
    return this.marketplaceService.getProducts();
  }

  @Get('products/:id')
  @ApiOperation({ summary: 'Fetch precise specifications of a B2B hardware SKU' })
  @ApiParam({ name: 'id', description: 'Product Database Key' })
  getProductById(@Param('id') id: string): B2BProduct {
    return this.marketplaceService.getProductById(id);
  }

  @Post('products')
  @ApiOperation({ summary: 'Register a new manufacturing variant product SKU (Manufacturer/Vendor)' })
  @ApiBody({ type: CreateProductDto })
  createProduct(@Body() body: CreateProductDto) {
    return this.marketplaceService.createProduct(body);
  }

  @Post('vendor/:userId/register')
  @ApiOperation({ summary: 'Submit corporate KYC to register as an authorized manufacturing vendor' })
  @ApiParam({ name: 'userId', description: 'Merchant Admin User UUID' })
  @ApiBody({ type: VendorRegisterDto })
  registerVendor(@Param('userId') userId: string, @Body() body: VendorRegisterDto) {
    return this.marketplaceService.registerVendor(userId, body);
  }

  @Get('vendor/:userId')
  @ApiOperation({ summary: 'Retrieve verified vendor profile details' })
  @ApiParam({ name: 'userId', description: 'Merchant Admin User UUID' })
  getVendor(@Param('userId') userId: string) {
    return this.marketplaceService.getVendor(userId);
  }

  @Get('orders')
  @ApiOperation({ summary: 'Fetch overall B2B platform logistics purchase orders' })
  @ApiQuery({ name: 'userId', required: false, description: 'Filter by merchant user ID' })
  getOrders(@Query('userId') userId?: string): B2BOrder[] {
    return this.marketplaceService.getOrders(userId);
  }

  @Post('orders/checkout/:userId')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Checkout and dispatch B2B procurement cart' })
  @ApiParam({ name: 'userId', description: 'Ordering Merchant User UUID' })
  @ApiBody({ type: CheckoutDto })
  checkout(@Param('userId') userId: string, @Body() body: CheckoutDto) {
    return this.marketplaceService.checkoutCart(userId, body.items, body.paymentMethod, body.paymentGateway);
  }

  @Post('orders/:id/return')
  @ApiOperation({ summary: 'Trigger an SLA returns request on a delivered procurement order' })
  @ApiParam({ name: 'id', description: 'Order ID' })
  @ApiBody({ type: ReturnRequestDto })
  requestReturn(@Param('id') id: string, @Body() body: ReturnRequestDto) {
    return this.marketplaceService.triggerOrderReturn(id, body.reason);
  }

  @Get('warehouses')
  @ApiOperation({ summary: 'Fetch all regional physical inventory warehouses' })
  getWarehouses(): B2BWarehouse[] {
    return this.marketplaceService.getWarehouses();
  }
}
