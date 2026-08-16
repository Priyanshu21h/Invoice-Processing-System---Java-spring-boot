import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import { useEffect, useState } from "react";

import { createInvoice } from "../api/invoiceApi";
import { getCustomers } from "../api/customerApi";
import { getProducts } from "../api/productApi";
import { toast } from "react-toastify";

function CreateInvoice() {
    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);

    const [selectedCustomer, setSelectedCustomer] = useState("");

    const [discount, setDiscount] = useState(0);

    const [invoiceItems, setInvoiceItems] = useState([
        {
            productId: "",
            quantity: 1,
            price: 0,
            total: 0
        }
    ]);
    useEffect(() => {

        loadCustomers();

        loadProducts();

    }, []);
    const addProductRow = () => {

        setInvoiceItems([
            ...invoiceItems,
            {
                productId: "",
                quantity: 1,
                price: 0,
                total: 0
            }
        ]);

    };
    const removeProductRow = (index) => {
        if (invoiceItems.length === 1) return;

        const updatedItems = invoiceItems.filter((_, i) => i !== index);

        setInvoiceItems(updatedItems);
    };
    const handleProductChange = (index, productId) => {

        const selectedProduct = products.find(
            product => product.id === Number(productId)
        );

        const updatedItems = [...invoiceItems];

        updatedItems[index].productId = productId;
        if (!selectedProduct) return;
        updatedItems[index].price = selectedProduct.price;

        updatedItems[index].total =
            selectedProduct.price * updatedItems[index].quantity;

        setInvoiceItems(updatedItems);

    };
    const handleQuantityChange = (index, quantity) => {

        const updatedItems = [...invoiceItems];

        updatedItems[index].quantity = Number(quantity);

        updatedItems[index].total =
            updatedItems[index].price *
            updatedItems[index].quantity;

        setInvoiceItems(updatedItems);

    };
    const calculateGrandTotal = () => {

        const subtotal = invoiceItems.reduce(

            (sum, item) => sum + item.total,

            0

        );

        return subtotal - (subtotal * discount / 100);

    };
    const handleGenerateInvoice = async () => {

        if (!selectedCustomer) {

            toast.error("Please select a customer.");

            return;

        }

        if (invoiceItems.length === 0) {

            toast.error("Please add at least one product.");

            return;

        }

        try {

            const invoiceData = {

                customerId: Number(selectedCustomer),

                discountPercent: discount,

                items: invoiceItems.map(item => ({

                    productId: Number(item.productId),

                    quantity: item.quantity

                }))

            };

            const response = await createInvoice(invoiceData);

            toast.success("Invoice generated successfully.");

            console.log(response.data);

            // Optional: Reset form after success
            setSelectedCustomer("");
            setDiscount(0);
            setInvoiceItems([
                {
                    productId: "",
                    quantity: 1,
                    price: 0,
                    total: 0
                }
            ]);

        } catch (error) {

            console.log(error);

            toast.error(
                error.response?.data?.message || "Failed to generate invoice."
            );

        }

    };
    const loadCustomers = async () => {

        try {

            const response = await getCustomers();

            setCustomers(response.data.content);

        } catch (error) {

            console.log(error);

            toast.error("Failed to load customers.");

        }

    };

    const loadProducts = async () => {

        try {

            const response = await getProducts();

            setProducts(response.data.content);

        } catch (error) {

            console.log(error);

            toast.error("Failed to load products.");

        }

    };
    return (

        <>
            <Navbar />

            <div className="dashboard-container">

                <Sidebar />

                <div className="dashboard-content">

                    <h1>Create Invoice</h1>

                    <h3>Select Customer</h3>

                    <select
                        value={selectedCustomer}
                        onChange={(e) => setSelectedCustomer(e.target.value)}
                    >

                        <option value="">
                            Select Customer
                        </option>

                        {customers.map(customer => (

                            <option
                                key={customer.id}
                                value={customer.id}
                            >
                                {customer.name}
                            </option>

                        ))}

                    </select>

                    <br /><br />

                    <table>

                        <thead>

                            <tr>

                                <th>Product</th>
                                <th>Price</th>
                                <th>Quantity</th>
                                <th>Total</th>
                                <th>Action</th>

                            </tr>

                        </thead>

                        <tbody>

                            {invoiceItems.map((item, index) => (

                                <tr key={index}>

                                    <td>

                                        <select
                                            value={item.productId}
                                            onChange={(e) =>
                                                handleProductChange(index, e.target.value)
                                            }
                                        >

                                            <option value="">
                                                Select Product
                                            </option>

                                            {products.map(product => (

                                                <option
                                                    key={product.id}
                                                    value={product.id}
                                                >
                                                    {product.name}
                                                </option>

                                            ))}

                                        </select>

                                    </td>

                                    <td>

                                        ₹{item.price}

                                    </td>

                                    <td>

                                        <input
                                            type="number"
                                            min="1"
                                            value={item.quantity}
                                            onChange={(e) =>
                                                handleQuantityChange(index, e.target.value)
                                            }
                                        />

                                    </td>

                                    <td>

                                        ₹{item.total}

                                    </td>
                                    <td>

                                        <button
                                            onClick={() => removeProductRow(index)}
                                        >
                                            Remove
                                        </button>

                                    </td>

                                </tr>

                            ))}

                        </tbody>

                    </table>

                    <br />

                    <button onClick={addProductRow}>

                        + Add Product

                    </button>

                    <br /><br />

                    <h3>Discount (%)</h3>

                    <input
                        type="number"
                        min="0"
                        max="100"
                        value={discount}
                        onChange={(e) =>
                            setDiscount(Math.min(100, Math.max(0, Number(e.target.value))))
                        }
                        placeholder="Discount (%)"
                    />

                    <br /><br />

                    <h2>Grand Total : ₹{calculateGrandTotal()}</h2>

                    <button onClick={handleGenerateInvoice}>

                        Generate Invoice

                    </button>

                </div>

            </div>

        </>

    );

}

export default CreateInvoice;