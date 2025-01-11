import { ApiResponse, RepairOrder } from '@/types';
import axiosInstance from './axios';

export interface CreateRepairOrderDTO {
  type: string;
  description: string;
  location: string;
  appointmentTime?: string;
  images?: File[];
}

export interface UpdateRepairOrderDTO {
  status?: string;
  description?: string;
  appointmentTime?: string;
}

export interface QueryRepairOrderParams {
  page?: number;
  size?: number;
  status?: string;
  type?: string;
  startDate?: string;
  endDate?: string;
}

export const repairApi = {
  // 创建工单
  createOrder: async (data: CreateRepairOrderDTO): Promise<ApiResponse<RepairOrder>> => {
    const formData = new FormData();
    Object.entries(data).forEach(([key, value]) => {
      if (key === 'images' && value) {
        (value as File[]).forEach((file) => {
          formData.append('images', file);
        });
      } else if (value !== undefined) {
        formData.append(key, value.toString());
      }
    });
    return axiosInstance.post('/repair-orders', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  // 获取工单列表
  getOrders: async (params: QueryRepairOrderParams): Promise<ApiResponse<{
    total: number;
    items: RepairOrder[];
  }>> => {
    return axiosInstance.get('/repair-orders', { params });
  },

  // 获取工单详情
  getOrderById: async (id: number): Promise<ApiResponse<RepairOrder>> => {
    return axiosInstance.get(`/repair-orders/${id}`);
  },

  // 更新工单状态
  updateOrder: async (id: number, data: UpdateRepairOrderDTO): Promise<ApiResponse<RepairOrder>> => {
    return axiosInstance.put(`/repair-orders/${id}`, data);
  },

  // 取消工单
  cancelOrder: async (id: number): Promise<ApiResponse<void>> => {
    return axiosInstance.delete(`/repair-orders/${id}`);
  },

  // 评价工单
  rateOrder: async (id: number, rating: number, comment?: string): Promise<ApiResponse<void>> => {
    return axiosInstance.post(`/repair-orders/${id}/rate`, { rating, comment });
  },

  // 获取我的工单统计
  getMyOrderStats: async (): Promise<ApiResponse<{
    total: number;
    pending: number;
    processing: number;
    completed: number;
  }>> => {
    return axiosInstance.get('/repair-orders/my-stats');
  },

  // 上传工单相关图片
  uploadImages: async (orderId: number, images: File[]): Promise<ApiResponse<string[]>> => {
    const formData = new FormData();
    images.forEach((image) => {
      formData.append('images', image);
    });
    return axiosInstance.post(`/repair-orders/${orderId}/images`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
};