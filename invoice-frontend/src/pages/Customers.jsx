import { useEffect, useState } from "react";
import Swal from "sweetalert2";
import {
    getCustomers,
    createCustomer,
    updateCustomer,
    deleteCustomer
} from "../api/customerApi";
import { toast } from "react-toastify";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

function Customers() {

    const [customers, setCustomers] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        phone: "",
        address: ""
    });
    const [searchTerm, setSearchTerm] = useState("");

    const [editingId, setEditingId] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadCustomers();
    }, []);
    useEffect(() => {
        loadCustomers(searchTerm, page);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [page]);

    const loadCustomers = async (name = searchTerm, pageNum = page) => {
        setLoading(true);
        try {
            const response = await getCustomers(name, pageNum);
            setCustomers(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error(error);
            toast.error("Failed to load customers.");
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

    };
    const handleSearchChange = (e) => {
        const value = e.target.value;
        setSearchTerm(value);
        setPage(0);
        loadCustomers(value, 0);
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            if (editingId) {

                await updateCustomer(editingId, formData);

                toast.success("Customer updated successfully.");

            } else {

                await createCustomer(formData);

                toast.success("Customer created successfully.");

            }

            setFormData({
                name: "",
                email: "",
                phone: "",
                address: ""
            });

            setEditingId(null);

            loadCustomers();

        } catch (error) {

            console.error(error);

            toast.error("Operation failed.");

        }

    };

    const handleEdit = (customer) => {

        setEditingId(customer.id);

        setFormData({
            name: customer.name,
            email: customer.email,
            phone: customer.phone,
            address: customer.address
        });

    };

    const handleDelete = async (id) => {

        const result = await Swal.fire({
            title: "Delete this customer?",
            text: "This action cannot be undone.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Yes, delete",
            cancelButtonText: "Cancel",
            confirmButtonColor: "#dc2626"
        });

        if (!result.isConfirmed) return;

        try {

            await deleteCustomer(id);

            loadCustomers();

            toast.success("Customer deleted successfully.");

        } catch (error) {

            console.error(error);

            const message = error.response?.data?.message || "Failed to delete customer.";
            toast.error(message);

        }

    };

    return (

        <>

            <Navbar />

            <div className="dashboard-container">

                <Sidebar />

                <div className="dashboard-content">

                    <h1>Customers</h1>
                    <input
                        type="text"
                        placeholder="Search by name..."
                        value={searchTerm}
                        onChange={handleSearchChange}
                        style={{ marginBottom: "12px" }}
                    />

                    <div className="customer-form">

                        <form onSubmit={handleSubmit}>

                            <input
                                type="text"
                                name="name"
                                placeholder="Name"
                                value={formData.name}
                                onChange={handleChange}
                            />

                            <input
                                type="email"
                                name="email"
                                placeholder="Email"
                                value={formData.email}
                                onChange={handleChange}
                            />

                            <input
                                type="text"
                                name="phone"
                                placeholder="Phone"
                                value={formData.phone}
                                onChange={handleChange}
                            />

                            <input
                                type="text"
                                name="address"
                                placeholder="Address"
                                value={formData.address}
                                onChange={handleChange}
                            />

                            <button type="submit">
                                {editingId ? "Update Customer" : "Add Customer"}
                            </button>

                        </form>

                    </div>

                    <br />

                    {loading ? (
                        <p>Loading customers...</p>
                    ) : (<table border="1" cellPadding="10">

                        <thead>

                            <tr>

                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Address</th>
                                <th>Actions</th>

                            </tr>

                        </thead>

                        <tbody>

                            {customers.map((customer) => (

                                <tr key={customer.id}>

                                    <td>{customer.name}</td>
                                    <td>{customer.email}</td>
                                    <td>{customer.phone}</td>
                                    <td>{customer.address}</td>

                                    <td>

                                        <button
                                            onClick={() => handleEdit(customer)}
                                        >
                                            Edit
                                        </button>

                                        {" "}

                                        <button
                                            onClick={() => handleDelete(customer.id)}
                                        >
                                            Delete
                                        </button>

                                    </td>

                                </tr>

                            ))}

                        </tbody>

                    </table>)}

                    <div style={{ marginTop: "12px" }}>
                        <button disabled={page === 0} onClick={() => setPage(page - 1)}>
                            Prev
                        </button>
                        <span style={{ margin: "0 10px" }}>
                            Page {page + 1} of {totalPages}
                        </span>
                        <button disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
                            Next
                        </button>
                    </div>

                </div>

            </div>

        </>

    );

}

export default Customers;