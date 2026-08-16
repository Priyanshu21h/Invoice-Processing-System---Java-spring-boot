import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

import {
    getInvoiceById,
    downloadInvoicePdf
} from "../api/invoiceApi";

function InvoiceDetails() {

    const { id } = useParams();

    const navigate = useNavigate();

    const [invoice, setInvoice] = useState(null);

    useEffect(() => {

        loadInvoice();

    }, []);

    const loadInvoice = async () => {

        try {

            const response = await getInvoiceById(id);

            setInvoice(response.data);

        } catch (error) {

            console.log(error);

        }

    };

    const handleDownload = async () => {

        try {

            const response = await downloadInvoicePdf(id);

            const file = new Blob(
                [response.data],
                { type: "application/pdf" }
            );

            const url = window.URL.createObjectURL(file);

            const link = document.createElement("a");

            link.href = url;

            link.download = `${invoice.invoiceNumber}.pdf`;

            document.body.appendChild(link);

            link.click();

            document.body.removeChild(link);

            window.URL.revokeObjectURL(url);

        } catch (error) {

            console.log(error);

            alert("Failed to download PDF.");

        }

    };

    if (!invoice) {

        return <h2>Loading...</h2>;

    }

    return (

        <>
            <Navbar />

            <div className="dashboard-container">

                <Sidebar />

                <div className="dashboard-content">

                    <button
                        className="back-btn"
                        onClick={() => navigate("/invoices")}
                    >
                        ← Back
                    </button>
                    <h1>Invoice Details</h1>

                    <h2>{invoice.invoiceNumber}</h2>

                    <p>
                        <strong>Customer:</strong> {invoice.customerName}
                    </p>

                    <p>
                        <strong>Date:</strong>{" "}
                        {new Date(invoice.invoiceDate).toLocaleString()}
                    </p>

                    <table className="crud-table">

                        <thead>

                            <tr>

                                <th>Product</th>
                                <th>Price</th>
                                <th>Quantity</th>
                                <th>Total</th>

                            </tr>

                        </thead>

                        <tbody>

                            {invoice.items.map((item, index) => (

                                <tr key={index}>

                                    <td>{item.productName}</td>
                                    <td>₹{item.priceAtSale}</td>
                                    <td>{item.quantity}</td>
                                    <td>₹{item.lineTotal}</td>

                                </tr>

                            ))}

                        </tbody>

                    </table>

                    <br />

                    <h3>GST : ₹{invoice.gstAmount}</h3>

                    <h3>Discount : {invoice.discountPercent}%</h3>

                    <h2>Grand Total : ₹{invoice.totalAmount}</h2>

                    <br />

                    <button onClick={handleDownload}>
                        Download PDF
                    </button>

                </div>

            </div>

        </>

    );

}

export default InvoiceDetails;