import axiosInstance from "./axiosInstance";

export const getProducts = (name = "", page = 0, size = 10) => {
    return axiosInstance.get("/products", {
        params: { ...(name ? { name } : {}), page, size }
    });
};

export const getProductById = (id) => {
    return axiosInstance.get(`/products/${id}`);
};

export const createProduct = (product) => {
    return axiosInstance.post("/products", product);
};

export const updateProduct = (id, product) => {
    return axiosInstance.put(`/products/${id}`, product);
};

export const deleteProduct = (id) => {
    return axiosInstance.delete(`/products/${id}`);
};