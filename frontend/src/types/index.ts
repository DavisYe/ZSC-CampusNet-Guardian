// 用户相关类型定义
export interface User {
  id: number;
  username: string;
  studentId?: string;
  email?: string;
  phone?: string;
  roles: string[];
}

// 认证相关类型定义
export interface LoginForm {
  username: string;
  password: string;
}

export interface RegisterForm extends LoginForm {
  studentId: string;
  email?: string;
  phone?: string;
}

// 工单相关类型定义
export enum RepairOrderStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  ESCALATED = 'ESCALATED',
  COMPLETED = 'COMPLETED'
}

export enum RepairOrderType {
  NETWORK = 'NETWORK',
  HARDWARE = 'HARDWARE',
  SOFTWARE = 'SOFTWARE',
  OTHER = 'OTHER'
}

export interface TimelineEvent {
  timestamp: string;
  status: RepairOrderStatus;
  description: string;
  operator?: string;
}

export interface RepairOrder {
  id: number;
  userId: number;
  type: RepairOrderType;
  status: RepairOrderStatus;
  description: string;
  location: string;
  appointmentTime?: string;
  images?: string[];
  createdAt: string;
  updatedAt: string;
  timeline: TimelineEvent[];
  rating?: {
    score: number;
    comment?: string;
    createdAt: string;
  };
}

// 知识库相关类型定义
export interface KnowledgeCategory {
  id: number;
  name: string;
  description?: string;
}

export interface KnowledgeArticle {
  id: number;
  categoryId: number;
  title: string;
  content: string;
  tags?: string[];
  createdAt: string;
  updatedAt: string;
}

// API响应类型定义
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}