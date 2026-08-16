import { NavLink } from "react-router-dom";

function Sidebar() {

    return (

        <div className="sidebar">

            <NavLink to="/dashboard">Dashboard</NavLink>

            <NavLink to="/customers">Customers</NavLink>

            <NavLink to="/products">Products</NavLink>

            <NavLink to="/invoices">Invoices</NavLink>

            <NavLink to="/create-invoice">Create Invoice</NavLink>

        </div>

    );

}

export default Sidebar;