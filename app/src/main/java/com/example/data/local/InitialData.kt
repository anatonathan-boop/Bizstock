package com.example.data.local

import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.model.Purchase
import com.example.data.model.Sale
import com.example.data.model.StockMovement
import com.example.data.model.Supplier

object InitialData {
    val initialProducts = listOf(
        Product(
            id = "prod-001",
            sku = "TECH-LAP-001",
            name = "PC Portable UltraBook Pro 15\"",
            category = "Informatique",
            purchasePrice = 750.00,
            sellingPrice = 1199.00,
            currentStock = 18,
            minStockAlert = 5,
            unit = "Unité",
            description = "Intel Core i7, 16Go RAM, SSD 512Go, écran FHD antireflet",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-002",
            sku = "TECH-SCR-002",
            name = "Écran 27\" 4K UHD UltraSharp",
            category = "Périphériques",
            purchasePrice = 280.00,
            sellingPrice = 449.00,
            currentStock = 8,
            minStockAlert = 5,
            unit = "Unité",
            description = "Dalle IPS 60Hz, hub USB-C 90W, pied pivotant réglable",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-003",
            sku = "PERI-KEY-003",
            name = "Clavier Mécanique Sans Fil Pro",
            category = "Périphériques",
            purchasePrice = 65.00,
            sellingPrice = 129.00,
            currentStock = 4, // Low stock alert!
            minStockAlert = 6,
            unit = "Unité",
            description = "Switches Brown silencieux, rétroéclairage blanc, multi-connexion",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-004",
            sku = "PERI-MOU-004",
            name = "Souris Ergonomique Laser Verticale",
            category = "Périphériques",
            purchasePrice = 35.00,
            sellingPrice = 79.90,
            currentStock = 22,
            minStockAlert = 8,
            unit = "Unité",
            description = "Prise en main naturelle 57°, capteur 4000 DPI précis",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-005",
            sku = "NET-ROU-005",
            name = "Routeur Wi-Fi 6 Enterprise AX6000",
            category = "Réseau",
            purchasePrice = 145.00,
            sellingPrice = 259.00,
            currentStock = 12,
            minStockAlert = 4,
            unit = "Unité",
            description = "8 ports Gigabit, VPN matériel sécurisé, couverture 350 m²",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-006",
            sku = "STO-NAS-006",
            name = "Serveur NAS 4 Baies Quad-Core",
            category = "Stockage",
            purchasePrice = 380.00,
            sellingPrice = 599.00,
            currentStock = 3, // Low stock alert!
            minStockAlert = 5,
            unit = "Unité",
            description = "Processeur Celeron J4125, 4Go DDR4 extensible, 2x 2.5GbE",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-007",
            sku = "STO-SSD-007",
            name = "Disque SSD NVMe Pro 2To Gen4",
            category = "Stockage",
            purchasePrice = 95.00,
            sellingPrice = 169.00,
            currentStock = 35,
            minStockAlert = 10,
            unit = "Unité",
            description = "Vitesses 7400 Mo/s en lecture, dissipateur thermique en aluminium",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-008",
            sku = "AUD-HEA-008",
            name = "Casque Sans Fil ANC Bureau Pro",
            category = "Audio",
            purchasePrice = 110.00,
            sellingPrice = 199.00,
            currentStock = 0, // Rupture de stock!
            minStockAlert = 5,
            unit = "Unité",
            description = "Réduction active du bruit, micro antibruit beamforming certifié Teams",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-009",
            sku = "POW-UPS-009",
            name = "Onduleur Line-Interactive 1500VA",
            category = "Alimentation",
            purchasePrice = 170.00,
            sellingPrice = 289.00,
            currentStock = 6,
            minStockAlert = 3,
            unit = "Unité",
            description = "6 prises secourues avec AVR, écran LCD de diagnostic",
            syncStatus = "SYNCED"
        ),
        Product(
            id = "prod-010",
            sku = "LOG-SCA-010",
            name = "Lecteur Code-Barres 2D / QR Sans Fil",
            category = "Logistique",
            purchasePrice = 55.00,
            sellingPrice = 115.00,
            currentStock = 15,
            minStockAlert = 5,
            unit = "Unité",
            description = "Portée 50m, batterie longue autonomie, support de charge inclus",
            syncStatus = "SYNCED"
        )
    )

    val initialCustomers = listOf(
        Customer(
            id = "cust-001",
            name = "Alexandre Dupont",
            company = "Nexora Solutions",
            email = "contact@nexora.fr",
            phone = "+33 1 42 68 00 12",
            city = "Paris",
            balance = 3450.00,
            totalOrders = 14
        ),
        Customer(
            id = "cust-002",
            name = "Claire Marchand",
            company = "InnoTech Industries",
            email = "c.marchand@innotech.com",
            phone = "+33 4 78 12 34 56",
            city = "Lyon",
            balance = 1200.00,
            totalOrders = 8
        ),
        Customer(
            id = "cust-003",
            name = "Karim Bensaid",
            company = "Cabinet Stratégie B2B",
            email = "kbensaid@strategieb2b.fr",
            phone = "+33 5 56 78 90 12",
            city = "Bordeaux",
            balance = 0.00,
            totalOrders = 22
        ),
        Customer(
            id = "cust-004",
            name = "Sophie Leroy",
            company = "LogiGlobal France",
            email = "sophie.leroy@logiglobal.eu",
            phone = "+33 3 20 45 67 89",
            city = "Lille",
            balance = 560.00,
            totalOrders = 5
        )
    )

    val initialSuppliers = listOf(
        Supplier(
            id = "supp-001",
            name = "Silicon Micro Distribution",
            company = "Silicon Micro SA",
            email = "orders@siliconmicro.com",
            phone = "+33 1 89 22 11 00",
            category = "Composants & Périphériques",
            activeOrders = 2,
            rating = 4.9f
        ),
        Supplier(
            id = "supp-002",
            name = "EuroNetworks Logistique",
            company = "EuroNet Log SAS",
            email = "contact@euronetworks.eu",
            phone = "+33 4 91 33 22 11",
            category = "Réseaux & Télécoms",
            activeOrders = 1,
            rating = 4.7f
        ),
        Supplier(
            id = "supp-003",
            name = "Global Storage Supplies",
            company = "GSS Logistics",
            email = "support@gss-storage.com",
            phone = "+33 2 40 55 66 77",
            category = "Stockage & Mémoires",
            activeOrders = 0,
            rating = 4.8f
        )
    )

    val initialSales = listOf(
        Sale(
            id = "sale-001",
            invoiceNumber = "FAC-2026-0891",
            customerName = "Nexora Solutions",
            totalAmount = 2398.00,
            paymentStatus = "PAYÉE",
            date = System.currentTimeMillis() - 86400000L * 1,
            itemsSummary = "2x PC Portable UltraBook Pro 15\"",
            itemsCount = 2
        ),
        Sale(
            id = "sale-002",
            invoiceNumber = "FAC-2026-0892",
            customerName = "InnoTech Industries",
            totalAmount = 898.00,
            paymentStatus = "PAYÉE",
            date = System.currentTimeMillis() - 86400000L * 2,
            itemsSummary = "2x Écran 27\" 4K UHD UltraSharp",
            itemsCount = 2
        ),
        Sale(
            id = "sale-003",
            invoiceNumber = "FAC-2026-0893",
            customerName = "Cabinet Stratégie B2B",
            totalAmount = 518.00,
            paymentStatus = "EN_ATTENTE",
            date = System.currentTimeMillis() - 86400000L * 3,
            itemsSummary = "2x Routeur Wi-Fi 6 Enterprise AX6000",
            itemsCount = 2
        ),
        Sale(
            id = "sale-004",
            invoiceNumber = "FAC-2026-0894",
            customerName = "LogiGlobal France",
            totalAmount = 345.00,
            paymentStatus = "PAYÉE",
            date = System.currentTimeMillis() - 86400000L * 5,
            itemsSummary = "3x Lecteur Code-Barres 2D / QR Sans Fil",
            itemsCount = 3
        )
    )

    val initialPurchases = listOf(
        Purchase(
            id = "purch-001",
            orderNumber = "ACH-2026-0420",
            supplierName = "Silicon Micro SA",
            totalAmount = 3750.00,
            status = "REÇU",
            date = System.currentTimeMillis() - 86400000L * 4,
            itemsSummary = "5x PC Portable UltraBook Pro 15\"",
            itemsCount = 5
        ),
        Purchase(
            id = "purch-002",
            orderNumber = "ACH-2026-0421",
            supplierName = "EuroNet Log SAS",
            totalAmount = 1450.00,
            status = "REÇU",
            date = System.currentTimeMillis() - 86400000L * 6,
            itemsSummary = "10x Routeur Wi-Fi 6 Enterprise AX6000",
            itemsCount = 10
        ),
        Purchase(
            id = "purch-003",
            orderNumber = "ACH-2026-0422",
            supplierName = "GSS Logistics",
            totalAmount = 950.00,
            status = "EN_COURS",
            date = System.currentTimeMillis() - 86400000L * 1,
            itemsSummary = "10x Disque SSD NVMe Pro 2To Gen4",
            itemsCount = 10
        )
    )

    val initialMovements = listOf(
        StockMovement(
            id = "mov-001",
            productId = "prod-001",
            productName = "PC Portable UltraBook Pro 15\"",
            type = "SORTIE",
            quantity = 2,
            reason = "Vente client FAC-2026-0891",
            date = System.currentTimeMillis() - 86400000L * 1
        ),
        StockMovement(
            id = "mov-002",
            productId = "prod-002",
            productName = "Écran 27\" 4K UHD UltraSharp",
            type = "SORTIE",
            quantity = 2,
            reason = "Vente client FAC-2026-0892",
            date = System.currentTimeMillis() - 86400000L * 2
        ),
        StockMovement(
            id = "mov-003",
            productId = "prod-001",
            productName = "PC Portable UltraBook Pro 15\"",
            type = "ENTRÉE",
            quantity = 5,
            reason = "Réception fournisseur ACH-2026-0420",
            date = System.currentTimeMillis() - 86400000L * 4
        ),
        StockMovement(
            id = "mov-004",
            productId = "prod-008",
            productName = "Casque Sans Fil ANC Bureau Pro",
            type = "AJUSTEMENT",
            quantity = 0,
            reason = "Inventaire physique : rupture constatée",
            date = System.currentTimeMillis() - 86400000L * 3
        )
    )
}
