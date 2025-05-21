import axios from 'axios';

const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
});

export interface PaginatedResponse<T> {
    content: T[];
    currentPage: number;
    totalRecords: number;
    recordsPerPage: number;
    recordsThisPage: number;
    totalPages: number;
    previousPages: number[];
    nextPages: number[];
}

export default axiosClient;
