import axiosInstance from "./axiosInstance";

export const getCustomerById = (id) => {
    return axiosInstance.get(`/customers/${id}`);
};

export const createCustomer = (customer) => {
    return axiosInstance.post("/customers", customer);
};

export const updateCustomer = (id, customer) => {
    return axiosInstance.put(`/customers/${id}`, customer);
};

export const deleteCustomer = (id) => {
    return axiosInstance.delete(`/customers/${id}`);
};
export const getCustomers = (name = "", page = 0, size = 10) => {
    return axiosInstance.get("/customers", {
        params: { ...(name ? { name } : {}), page, size }
    });
};