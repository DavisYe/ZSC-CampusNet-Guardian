'use client';

import { create } from 'zustand';
import { User } from '@/types';
import { authApi } from '../api/auth';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (username: string, password: string) => Promise<void>;
  register: (data: {
    username: string;
    password: string;
    studentId: string;
    email?: string;
    phone?: string;
  }) => Promise<void>;
  logout: () => Promise<void>;
  checkAuth: () => Promise<void>;
}

export const useAuth = create<AuthState>((set) => ({
  user: null,
  token: typeof window !== 'undefined' ? localStorage.getItem('token') : null,
  isAuthenticated: false,
  isLoading: true,
  error: null,

  login: async (username: string, password: string) => {
    try {
      set({ isLoading: true, error: null });
      const response = await authApi.login({ username, password });
      const { token, user } = response.data;
      localStorage.setItem('token', token);
      set({ user, token, isAuthenticated: true, error: null });
    } catch {
      const error = '登录失败，请检查用户名和密码';
      set({ error });
      throw new Error(error);
    } finally {
      set({ isLoading: false });
    }
  },

  register: async (data) => {
    try {
      set({ isLoading: true, error: null });
      const response = await authApi.register(data);
      const { token, user } = response.data;
      localStorage.setItem('token', token);
      set({ user, token, isAuthenticated: true, error: null });
    } catch {
      const error = '注册失败，请稍后重试';
      set({ error });
      throw new Error(error);
    } finally {
      set({ isLoading: false });
    }
  },

  logout: async () => {
    try {
      set({ isLoading: true });
      await authApi.logout();
      localStorage.removeItem('token');
      set({
        user: null,
        token: null,
        isAuthenticated: false,
        error: null,
      });
    } catch {
      set({ error: '退出登录失败' });
    } finally {
      set({ isLoading: false });
    }
  },

  checkAuth: async () => {
    try {
      set({ isLoading: true });
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('No token found');
      }
      const response = await authApi.getCurrentUser();
      set({
        user: response.data,
        isAuthenticated: true,
        error: null,
      });
    } catch {
      localStorage.removeItem('token');
      set({
        user: null,
        token: null,
        isAuthenticated: false,
        error: '会话已过期，请重新登录',
      });
    } finally {
      set({ isLoading: false });
    }
  },
}));

export default useAuth;