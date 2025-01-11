import { ApiResponse, LoginForm, RegisterForm, User } from '@/types';
import axiosInstance from './axios';

export const authApi = {
  login: async (data: LoginForm): Promise<ApiResponse<{ token: string; user: User }>> => {
    return axiosInstance.post('/auth/login', data);
  },

  register: async (data: RegisterForm): Promise<ApiResponse<{ token: string; user: User }>> => {
    return axiosInstance.post('/auth/register', data);
  },

  getCurrentUser: async (): Promise<ApiResponse<User>> => {
    return axiosInstance.get('/auth/current-user');
  },

  logout: async (): Promise<ApiResponse<void>> => {
    return axiosInstance.post('/auth/logout');
  },

  forgotPassword: async (email: string): Promise<ApiResponse<void>> => {
    return axiosInstance.post('/auth/forgot-password', { email });
  },

  resetPassword: async (token: string, password: string): Promise<ApiResponse<void>> => {
    return axiosInstance.post('/auth/reset-password', { token, password });
  },

  updateProfile: async (data: Partial<User>): Promise<ApiResponse<User>> => {
    return axiosInstance.put('/auth/profile', data);
  },

  changePassword: async (oldPassword: string, newPassword: string): Promise<ApiResponse<void>> => {
    return axiosInstance.post('/auth/change-password', { oldPassword, newPassword });
  }
};