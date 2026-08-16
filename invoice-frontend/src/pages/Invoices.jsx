import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

import {
    getInvoices,
    deleteInvoice
} from "../api/invoiceApi";

function Invoices() {

    const [invoices, setInvoices] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {

        loadInvoices();

    }, []);

    const loadInvoices = async () => {

        try {

            const response = await getInvoices();

            setInvoices(response.data.content);

        } catch (error) {

            console.log(error);

        }

    };

    const handleDelete = async (id) => {

        if (!window.confirm("Delete this invoice?"))
            return;

        try {

            await deleteInvoice(id);

            loadInvoices();

            alert("Invoice deleted.");

        } catch (error) {

            console.log(error);

            alert("Delete failed.");

        }

    };

    return (

        <>
            <Navbar />

            <div className="dashboard-container">

                <Sidebar />

                <div className="dashboard-content">

                    <h1>Invoices</h1>

                    <table className="crud-table">

                        <thead>

                            <tr>

                                <th>ID</th>
                                <th>Invoice Number</th>
                                <th>Date</th>
                                <th>Total</th>
                                <th>Actions</th>

                            </tr>

                        </thead>

                        <tbody>

                            {invoices.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan="5"
                                        style={{
                                            textAlign: "center",
                                            padding: "25px"
                                        }}
                                    >
                                        No invoices found.
                                    </td>

                                </tr>

                            ) : (

                                invoices.map((invoice) => (

                                    <tr key={invoice.id}>

                                        <td>{invoice.id}</td>

                                        <td>{invoice.invoiceNumber}</td>

                                        <td>
                                            {new Date(
                                                invoice.invoiceDate
                                            ).toLocaleDateString()}
                                        </td>

                                        <td>
                                            ₹{Number(
                                                invoice.totalAmount
                                            ).toLocaleString()}
                                        </td>

                                        <td>

                                            <button
                                                className="action-btn view-btn"
                                                onClick={() =>
                                                    navigate(`/invoices/${invoice.id}`)
                                                }
                                            >
                                                View
                                            </button>

                                            <button
                                                className="action-btn delete-btn"
                                                onClick={() =>
                                                    handleDelete(invoice.id)
                                                }
                                            >
                                                Delete
                                            </button>

                                        </td>

                                    </tr>

                                ))

                            )}

                        </tbody>

                    </table>

                </div>

            </div>

        </>

    );

}

export default Invoices;