import { useEffect, useState } from "react";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

import { getDashboardStats } from "../api/invoiceApi";

function Dashboard() {

    const [stats, setStats] = useState({
        totalCustomers: 0,
        totalProducts: 0,
        totalInvoices: 0,
        totalSales: 0,
        lowStockCount: 0
    });

    useEffect(() => {
        loadDashboardStats();
    }, []);

    const loadDashboardStats = async () => {

        try {

            const response = await getDashboardStats();

            setStats(response.data);

        } catch (error) {

            console.log(error);

        }

    };

    return (

        <>
            <Navbar />

            <div className="dashboard-container">

                <Sidebar />

                <div className="dashboard-content">

                    <h1>Dashboard</h1>

                    <div className="card-grid">

                        <div className="card">
                            <h3>Total Customers</h3>
                            <p>{stats.totalCustomers}</p>
                        </div>

                        <div className="card">
                            <h3>Total Products</h3>
                            <p>{stats.totalProducts}</p>
                        </div>

                        <div className="card">
                            <h3>Total Invoices</h3>
                            <p>{stats.totalInvoices}</p>
                        </div>

                        <div className="card">
                            <h3>Total Revenue</h3>
                            <p>₹{Number(stats.totalSales).toLocaleString()}</p>
                        </div>

                        <div className="card">
                            <h3>Low Stock Products</h3>
                            <p>{stats.lowStockCount}</p>
                        </div>

                    </div>

                </div>

            </div>

        </>

    );

}

export default Dashboard;