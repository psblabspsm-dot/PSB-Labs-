package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order
import com.example.data.Product
import com.example.ui.AppViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val products by viewModel.products.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val walletState by viewModel.walletState.collectAsState()

    var activeTab by remember { mutableStateOf("procure") } // procure, vendor, warehouse
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var showCartDialog by remember { mutableStateOf(false) }
    var checkoutPaymentMethod by remember { mutableStateOf("WALLET") }
    var selectedGateway by remember { mutableStateOf("Cashfree") } // Cashfree, Razorpay, Pine Labs, CCAvenue, Paytm, Zaakpay

    // Tracking & Detail states
    var selectedTrackOrder by remember { mutableStateOf<Order?>(null) }
    var selectedInvoiceOrder by remember { mutableStateOf<Order?>(null) }

    // Vendor Wizard states
    var vendorRegistered by remember { mutableStateOf(false) }
    var vendorKYCStatus by remember { mutableStateOf("NOT_SUBMITTED") } // NOT_SUBMITTED, SUBMITTED, APPROVED
    var vendorPan by remember { mutableStateOf("") }
    var vendorGst by remember { mutableStateOf("") }
    var newProdName by remember { mutableStateOf("") }
    var newProdPrice by remember { mutableStateOf("") }
    var newProdVariant by remember { mutableStateOf("Standard Slate Blue") }
    var distributorDiscount by remember { mutableStateOf("15") } // %
    var retailerDiscount by remember { mutableStateOf("8") } // %
    var bulkFileProgress by remember { mutableStateOf(0.0f) }
    var isUploadingBulk by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sub-Header & Dynamic Segmented Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "B2B Marketplace Hub",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Enterprise multi-vendor procurement and warehouse logistics.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Cart FAB with Badge (Available on procure catalog tab)
            if (activeTab == "procure") {
                Box(contentAlignment = Alignment.TopEnd) {
                    FloatingActionButton(
                        onClick = { showCartDialog = true },
                        containerColor = Color(0xFFFF8C00),
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("open_cart_button")
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "View Cart", modifier = Modifier.size(18.dp))
                    }
                    val cartItemCount = cart.values.sum()
                    if (cartItemCount > 0) {
                        Badge(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            modifier = Modifier.offset(x = 2.dp, y = (-2).dp)
                        ) {
                            Text(text = cartItemCount.toString(), fontWeight = FontWeight.Bold, fontSize = 8.sp)
                        }
                    }
                }
            }
        }

        // Sub Navigation Segmented Tabs
        TabRow(
            selectedTabIndex = when(activeTab) {
                "procure" -> 0
                "vendor" -> 1
                "warehouse" -> 2
                else -> 0
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = activeTab == "procure",
                onClick = { activeTab = "procure" },
                text = { Text("Procure", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Storefront, null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = activeTab == "vendor",
                onClick = { activeTab = "vendor" },
                text = { Text("Vendor Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.AssignmentInd, null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = activeTab == "warehouse",
                onClick = { activeTab = "warehouse" },
                text = { Text("Logistics", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Warehouse, null, modifier = Modifier.size(18.dp)) }
            )
        }

        // Crossfading body contents
        Crossfade(targetState = activeTab, modifier = Modifier.weight(1f).fillMaxWidth()) { tab ->
            when (tab) {
                "procure" -> {
                    // Procurement Catalog view with Dynamic Pricing & Invoicing Operations
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search & Category selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search hardware devices...") },
                                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFF8C00)
                                )
                            )

                            // Voice search simulation
                            IconButton(onClick = { viewModel.showNotification("Listening for smart voice search query...") }) {
                                Icon(Icons.Default.Mic, null, tint = Color(0xFFFF8C00))
                            }
                        }

                        // Category Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "POS Devices", "Biometric", "Connectivity", "Thermal Printers").forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Filtered catalog
                            val filtered = products.filter {
                                val matchQuery = it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
                                val matchCat = selectedCategory == "All" || it.category.contains(selectedCategory.take(5), ignoreCase = true)
                                matchQuery && matchCat
                            }

                            // Product Recommendations section (Sleek Horizontal Panel)
                            if (searchQuery.isEmpty() && selectedCategory == "All" && products.isNotEmpty()) {
                                item {
                                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                        Text(
                                            text = "Recommended for Your Kiosk (B2B AI-Assisted)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Show top 3 items as recommended
                                            products.take(3).forEach { product ->
                                                Card(
                                                    modifier = Modifier
                                                        .width(240.dp)
                                                        .clickable { viewModel.showNotification("Selected recommended product: ${product.name}") },
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = when (product.iconName) {
                                                                    "atm" -> Icons.Default.Atm
                                                                    "fingerprint" -> Icons.Default.Fingerprint
                                                                    "router" -> Icons.Default.Router
                                                                    "receipt" -> Icons.Default.ReceiptLong
                                                                    "storefront" -> Icons.Default.Storefront
                                                                    else -> Icons.Default.Devices
                                                                },
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "98% Match Score",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = Color(0xFF2E7D32)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text(text = product.description, fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(text = "₹${product.price}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                                            val cartQty = cart[product] ?: 0
                                                            if (cartQty > 0) {
                                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                    IconButton(onClick = { viewModel.removeFromCart(product) }, modifier = Modifier.size(20.dp)) {
                                                                        Icon(Icons.Default.Remove, null, modifier = Modifier.size(12.dp))
                                                                    }
                                                                    Text(cartQty.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                                    IconButton(onClick = { viewModel.addToCart(product) }, modifier = Modifier.size(20.dp)) {
                                                                        Icon(Icons.Default.Add, null, modifier = Modifier.size(12.dp))
                                                                    }
                                                                }
                                                            } else {
                                                                Button(
                                                                    onClick = { viewModel.addToCart(product) },
                                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                                    modifier = Modifier.height(24.dp),
                                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                                ) {
                                                                    Text("Add", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "Active Procurement Items (${filtered.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            if (filtered.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No matching hardware items in local catalog.", color = Color.Gray, fontSize = 13.sp)
                                    }
                                }
                            } else {
                                items(filtered) { product ->
                                    B2BProductRow(product, cart[product] ?: 0, onAdd = {
                                        viewModel.addToCart(product)
                                    }, onRemove = {
                                        viewModel.removeFromCart(product)
                                    })
                                }
                            }

                            // Dynamic Retailer / Distributor Slab Notice
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Percent, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Dynamic SLA Margin Benefits Applied",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "Distributors enjoy flat 15% SLA off hardware; Retailers get 8% instant credit limit adjustments.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Orders List
                            item {
                                Text(
                                    text = "Procurement Orders & Tracking (${orders.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                )
                            }

                            if (orders.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                            Text("No recorded B2B orders.", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            } else {
                                items(orders) { order ->
                                    B2BOrderAdvancedRow(
                                        order = order,
                                        onTrack = { selectedTrackOrder = order },
                                        onInvoice = { selectedInvoiceOrder = order },
                                        onRepeat = {
                                            viewModel.showNotification("Re-queueing order elements to shopping basket...")
                                            viewModel.addNotification(
                                                title = "Order Repeated",
                                                message = "Your order items '${order.productNames}' have been re-added to your procurement basket.",
                                                type = "INFO"
                                            )
                                        },
                                        onCancel = {
                                            viewModel.showNotification("Order cancellation request dispatched under GST SLA protocols.")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "vendor" -> {
                    // Multi-Vendor registration & product uploading
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Vendor KYC Registration section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Store, null, tint = Color(0xFFFF8C00), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("B2B Vendor KYC & Registration", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                if (vendorKYCStatus == "APPROVED") {
                                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("Vendor Account Active", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2E7D32))
                                                Text("You are an authorized Surya OEM partner. You can now inject custom dynamic-pricing hardware variants.", fontSize = 11.sp, color = Color.Gray)
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Onboard your factory, manufacturing node, or bulk distributorship to supply POS systems directly to our 14K+ retail grid.",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    OutlinedTextField(
                                        value = vendorPan,
                                        onValueChange = { vendorPan = it },
                                        label = { Text("Company PAN") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = vendorGst,
                                        onValueChange = { vendorGst = it },
                                        label = { Text("GSTIN Identification") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Button(
                                        onClick = {
                                            if (vendorPan.isNotBlank() && vendorGst.isNotBlank()) {
                                                vendorKYCStatus = "APPROVED"
                                                viewModel.showNotification("Corporate Vendor KYC successfully approved!")
                                            } else {
                                                viewModel.showNotification("Please provide complete PAN & GST credentials.")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Authorize Corporate Vendor Profile", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Product Insertion & Dynamic Pricing Variant config
                        if (vendorKYCStatus == "APPROVED") {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PriceCheck, null, tint = Color(0xFF00C853), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Publish Product & Variant Pricing", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    OutlinedTextField(
                                        value = newProdName,
                                        onValueChange = { newProdName = it },
                                        label = { Text("Hardware Product Model") },
                                        placeholder = { Text("e.g. Mantra MFS110 Dual Biometric") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = newProdPrice,
                                            onValueChange = { newProdPrice = it },
                                            label = { Text("MSRP Base Price (₹)") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = newProdVariant,
                                            onValueChange = { newProdVariant = it },
                                            label = { Text("Variant Specs") },
                                            placeholder = { Text("e.g. 4G Black LTE") },
                                            modifier = Modifier.weight(1.2f),
                                            singleLine = true
                                        )
                                    }

                                    Text("Multi-Tier SLA Pricing Rules (Auto applied)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = distributorDiscount,
                                            onValueChange = { distributorDiscount = it },
                                            label = { Text("Distributor Disc %") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = retailerDiscount,
                                            onValueChange = { retailerDiscount = it },
                                            label = { Text("Retailer Disc %") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val pr = newProdPrice.toDoubleOrNull() ?: 0.0
                                            if (newProdName.isNotBlank() && pr > 0) {
                                                viewModel.showNotification("Product $newProdName added with dynamic pricing configurations.")
                                                newProdName = ""
                                                newProdPrice = ""
                                            } else {
                                                viewModel.showNotification("Product model and MSRP price required.")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Publish to Surya National Catalogue", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Bulk Catalogue CSV/Excel Importer
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.FileUpload, null, tint = Color(0xFF1E88E5), modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Bulk Upload (Excel / CSV)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    Text(
                                        text = "Download template schema, populate custom barcodes, inventory stock counts, and upload in one bulk batch.",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    if (isUploadingBulk) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            LinearProgressIndicator(
                                                progress = bulkFileProgress,
                                                color = Color(0xFFFF8C00),
                                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                                            )
                                            Text(
                                                text = "Uploading batch CSV... ${(bulkFileProgress * 100).toInt()}% • Parsing columns & validating checksums...",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color.Gray
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.showNotification("Excel CSV layout template saved to device.") },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("TEMPLATE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    isUploadingBulk = true
                                                    bulkFileProgress = 0.0f
                                                    coroutineScope.launch {
                                                        while (bulkFileProgress < 1.0f) {
                                                            kotlinx.coroutines.delay(200)
                                                            bulkFileProgress += 0.2f
                                                        }
                                                        isUploadingBulk = false
                                                        viewModel.showNotification("Bulk file uploaded: 45 new SKUs, 12 variants injected successfully.")
                                                        viewModel.addNotification(
                                                            title = "Bulk Upload Succeeded",
                                                            message = "OEM factory bulk stock manifest accepted. 45 hardware SKUs mapped to regional warehouses.",
                                                            type = "SUCCESS"
                                                        )
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                                                modifier = Modifier.weight(1.5f)
                                            ) {
                                                Text("UPLOAD SHEETS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "warehouse" -> {
                    // Warehouse & Inventory Management
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Regional Warehousing maps
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warehouse, null, tint = Color(0xFF8E24AA), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("National Logistics Nodes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Text(
                                    text = "Track space allocations, transit times, and dynamic inventories inside active regional fulfillment terminals.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )

                                WarehouseStatsRow("Bengaluru South Terminal", 0.72f, "14,500 SKUs", "Space: 72% used")
                                WarehouseStatsRow("Mumbai Logistics Hub", 0.38f, "8,120 SKUs", "Space: 38% used")
                                WarehouseStatsRow("Delhi Outer NCR Center", 0.94f, "21,450 SKUs", "CRITICAL / 94% full")
                            }
                        }

                        // Logistics delivery & Dispatch Timeline overview
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, null, tint = Color(0xFFFF8C00), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Active Shipments & Fleet Tracker", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Surface(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp)) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Bluedart Courier Cargo Flight #BD392", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                            Text("En route from Mumbai Terminal to Bengaluru Transit. Contains 120 Micro ATM biometric devices.", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }
                                }

                                Divider()

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Total Dispatches Today", fontSize = 11.sp, color = Color.Gray)
                                        Text("142 Shipments", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Column {
                                        Text("Ontime Delivery Index", fontSize = 11.sp, color = Color.Gray)
                                        Text("98.42% (Excellent)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00C853))
                                    }
                                    Column {
                                        Text("Logistics Transit SLA", fontSize = 11.sp, color = Color.Gray)
                                        Text("36.4 Hours Avg", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------------- INTERACTIVE MODALS & DETAIL VIEWS ----------------

    // 1. ADVANCED INTERACTIVE SHOPPING CART WITH GATEWAY SELECTIONS
    if (showCartDialog) {
        val totalAmount = cart.map { it.key.price * it.value }.sum()

        AlertDialog(
            onDismissRequest = { showCartDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("B2B Procurement Cart")
                    IconButton(onClick = { viewModel.clearCart() }) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear all", tint = Color.Red)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (cart.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                            Text("Your procurement basket is empty.", color = Color.Gray, fontSize = 13.sp)
                        }
                    } else {
                        // Scrollable cart items list
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 140.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            cart.forEach { (prod, qty) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = prod.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(text = "₹${prod.price} x$qty", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Text(text = "₹${prod.price * qty}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                            }
                        }

                        Divider()

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Basket Subtotal:", fontSize = 12.sp)
                            Text("₹${String.format("%,.2f", totalAmount)}", fontWeight = FontWeight.Bold)
                        }

                        // Gateway Integration selector
                        Text("Select Integration Payment Gateway:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Cashfree", "Razorpay", "Pine Labs", "CCAvenue", "Paytm", "Zaakpay").forEach { gw ->
                                FilterChip(
                                    selected = selectedGateway == gw,
                                    onClick = { selectedGateway = gw },
                                    label = { Text(gw, fontSize = 11.sp) }
                                )
                            }
                        }

                        // Auto Settlement check
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = true, onCheckedChange = {})
                            Text("Direct Settlement via dynamic ICICI Virtual Bank Node", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                if (cart.isNotEmpty()) {
                    Button(
                        onClick = {
                            viewModel.checkoutCart("GATEWAY_$selectedGateway")
                            showCartDialog = false
                            viewModel.addNotification(
                                title = "Payment Dispatched via $selectedGateway",
                                message = "Amount of ₹${String.format("%,.2f", totalAmount)} successfully processed and auto-reconciled.",
                                type = "SUCCESS"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        modifier = Modifier.testTag("checkout_submit_button")
                    ) {
                        Text("Pay ₹${String.format("%,.0f", totalAmount)} via $selectedGateway")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCartDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. TIMELINE ORDER TRACKING STEPS
    if (selectedTrackOrder != null) {
        val o = selectedTrackOrder!!
        AlertDialog(
            onDismissRequest = { selectedTrackOrder = null },
            title = { Text("Logistics Timeline Status - Order #${o.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Items: ${o.productNames}", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    // Vertical stepper simulation
                    TrackingStepperItem(title = "B2B Hardware Procurement Placed", desc = "Order received and mapped to Yes Bank Gateway.", active = true, completed = true)
                    TrackingStepperItem(title = "Fulfillment Hub Packing Done", desc = "Device serial numbers validated and GST billing calculated.", active = true, completed = true)
                    TrackingStepperItem(title = "In Transit (En Route Hub Terminal)", desc = "Assigned to Bluedart cargo fleet flight #BD392.", active = true, completed = false)
                    TrackingStepperItem(title = "Out for Kiosk Delivery", desc = "Surya field officer to verify STQC scanner validation.", active = false, completed = false)
                }
            },
            confirmButton = {
                Button(onClick = { selectedTrackOrder = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))) {
                    Text("OK")
                }
            }
        )
    }

    // 3. DIGITAL GST TAX INVOICE GENERATOR
    if (selectedInvoiceOrder != null) {
        val o = selectedInvoiceOrder!!
        val sub = o.totalAmount / 1.18
        val gst = o.totalAmount - sub

        AlertDialog(
            onDismissRequest = { selectedInvoiceOrder = null },
            title = { Text("Tax GST Invoice Generation") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("SURYA B2B GATEWAY SYSTEM", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                    Text("Authorized GST Tax Invoice Recipient Node", fontSize = 11.sp, color = Color.Gray)
                    Divider()

                    ProfileDetailRow("Invoice Identifier", "INV/2026/G-${o.id}")
                    ProfileDetailRow("Authorized Merchant", "Surya Digital Solutions")
                    ProfileDetailRow("GSTIN Reg ID", "29AAECS1234B1Z2")
                    ProfileDetailRow("Payment Mechanism", o.paymentMethod)
                    ProfileDetailRow("Dispatches Hub", "Bengaluru South Terminal")

                    Divider()

                    ProfileDetailRow("Items Costed (Taxable)", "₹${String.format("%,.2f", sub)}")
                    ProfileDetailRow("Central CGST (9.0%)", "₹${String.format("%,.2f", gst / 2)}")
                    ProfileDetailRow("State SGST (9.0%)", "₹${String.format("%,.2f", gst / 2)}")
                    ProfileDetailRow("Aggregate Invoice Amount", "₹${String.format("%,.2f", o.totalAmount)}")

                    Divider()

                    Text(
                        text = "Verified under Surya Security Standards. Dispatched to regional GST taxation database.",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showNotification("GST Invoice downloaded successfully as PDF.")
                        selectedInvoiceOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF EXPORT")
                }
            }
        )
    }
}

@Composable
fun B2BProductRow(product: Product, cartQty: Int, onAdd: () -> Unit, onRemove: () -> Unit) {
    val icon = when (product.iconName) {
        "atm" -> Icons.Default.Atm
        "fingerprint" -> Icons.Default.Fingerprint
        "router" -> Icons.Default.Router
        "receipt" -> Icons.Default.ReceiptLong
        "storefront" -> Icons.Default.Storefront
        else -> Icons.Default.Devices
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = product.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "₹${product.price}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Stock: ${product.stock}", fontSize = 11.sp, color = if (product.stock > 10) Color(0xFF2E7D32) else Color.Red)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Add/Subtract Counter
            if (cartQty > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = Color.Red, modifier = Modifier.size(14.dp))
                    }
                    Text(text = cartQty.toString(), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    IconButton(onClick = onAdd, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Color(0xFFFF8C00), modifier = Modifier.size(14.dp))
                    }
                }
            } else {
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("add_product_${product.id}").height(32.dp)
                ) {
                    Text("ADD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun B2BOrderAdvancedRow(
    order: Order,
    onTrack: () -> Unit,
    onInvoice: () -> Unit,
    onRepeat: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFFFF8C00), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = order.status,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = order.productNames, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Settled via: ${order.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
                Text(text = "Amt: ₹${String.format("%,.2f", order.totalAmount)}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Logistics & Invoice Quick action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTrack,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1f).height(30.dp)
                ) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TRACK", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onInvoice,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1.1f).height(30.dp)
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("GST BILL", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onRepeat,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.weight(1f).height(30.dp)
                ) {
                    Text("REPEAT", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onCancel, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Cancel, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun TrackingStepperItem(title: String, desc: String, active: Boolean, completed: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 2.dp)) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (completed) Color(0xFF00C853) else if (active) Color(0xFFFF8C00) else Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (completed) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
            Box(modifier = Modifier.width(2.dp).height(32.dp).background(if (completed) Color(0xFF00C853) else Color.LightGray))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (active || completed) MaterialTheme.colorScheme.onSurface else Color.Gray)
            Text(text = desc, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun WarehouseStatsRow(name: String, percentage: Float, count: String, details: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = count, fontSize = 11.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = percentage,
                color = if (percentage > 0.9f) Color.Red else if (percentage > 0.7f) Color(0xFFFFB300) else Color(0xFF00C853),
                trackColor = Color.LightGray.copy(alpha = 0.3f),
                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = details, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (percentage > 0.9f) Color.Red else Color.Gray)
        }
    }
}

