import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Swal from "sweetalert2";

import {
    getProducts,
    createProduct,
    updateProduct,
    deleteProduct
} from "../api/productApi";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

function Products() {

    const [products, setProducts] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        price: "",
        stockQuantity: "",
        gstPercent: ""
    });
    const [searchTerm, setSearchTerm] = useState("");

    const [editingId, setEditingId] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadProducts();
    }, []);

    useEffect(() => {
        loadProducts(searchTerm, page);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [page]);

    const loadProducts = async (name = searchTerm, pageNum = page) => {
        setLoading(true);
        try {
            const response = await getProducts(name, pageNum);
            setProducts(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.log(error);
            toast.error("Failed to load products.");
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
        loadProducts(value, 0);
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            if (editingId) {

                await updateProduct(editingId, formData);

                toast.success("Product updated successfully.");

            } else {

                await createProduct(formData);

                toast.success("Product created successfully.");

            }

            setFormData({
                name: "",
                price: "",
                stockQuantity: "",
                gstPercent: ""
            });

            setEditingId(null);

            loadProducts();

        } catch (error) {

            console.log(error);

            toast.error("Operation failed.");

        }

    };

    const handleEdit = (product) => {

        setEditingId(product.id);

        setFormData({
            name: product.name,
            price: product.price,
            stockQuantity: product.stockQuantity,
            gstPercent: product.gstPercent
        });

    };

    const handleDelete = async (id) => {

        const result = await Swal.fire({
            title: "Delete this product?",
            text: "This action cannot be undone.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Yes, delete",
            cancelButtonText: "Cancel",
            confirmButtonColor: "#dc2626"
        });

        if (!result.isConfirmed) return;

        try {

            await deleteProduct(id);

            loadProducts();

            toast.success("Product deleted successfully.");

        } catch (error) {

            console.log(error);

            const message = error.response?.data?.message || "Failed to delete product.";
            toast.error(message);

        }

    };

    return (

        <>
            <Navbar />

            <div className="dashboard-container">

                <Sidebar />

                <div className="dashboard-content">

                    <h1>Products</h1>
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
                                placeholder="Product Name"
                                value={formData.name}
                                onChange={handleChange}
                            />

                            <input
                                type="number"
                                name="price"
                                placeholder="Price"
                                value={formData.price}
                                onChange={handleChange}
                            />

                            <input
                                type="number"
                                name="stockQuantity"
                                placeholder="Stock"
                                value={formData.stockQuantity}
                                onChange={handleChange}
                            />

                            <input
                                type="number"
                                name="gstPercent"
                                placeholder="GST %"
                                value={formData.gstPercent}
                                onChange={handleChange}
                            />

                            <button type="submit">
                                {editingId ? "Update Product" : "Add Product"}
                            </button>

                        </form>

                    </div>

                    {loading ? (
                        <p>Loading products...</p>
                    ) : (<table>

                        <thead>

                            <tr>

                                <th>Name</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>GST %</th>
                                <th>Actions</th>

                            </tr>

                        </thead>

                        <tbody>

                            {products.map(product => (

                                <tr key={product.id}>

                                    <td>{product.name}</td>
                                    <td>₹ {product.price}</td>
                                    <td>{product.stockQuantity}</td>
                                    <td>{product.gstPercent}%</td>

                                    <td>

                                        <button
                                            onClick={() => handleEdit(product)}
                                        >
                                            Edit
                                        </button>

                                        <button
                                            onClick={() => handleDelete(product.id)}
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

export default Products;