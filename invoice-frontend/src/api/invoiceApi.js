import axiosInstance from "./axiosInstance";

export const getInvoices = () => {
    return axiosInstance.get("/invoices");
};

export const getInvoiceById = (id) => {
    return axiosInstance.get(`/invoices/${id}`);
};

export const createInvoice = (invoice) => {
    return axiosInstance.post("/invoices", invoice);
};

export const deleteInvoice = (id) => {
    return axiosInstance.delete(`/invoices/${id}`);
};

export const downloadInvoicePdf = (id) => {
    return axiosInstance.get(`/invoices/${id}/pdf`, {
        responseType: "blob"
    });
};

export const getDashboardStats = () => {
    return axiosInstance.get("/dashboard/stats");
};