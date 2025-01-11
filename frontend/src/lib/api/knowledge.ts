import { ApiResponse, KnowledgeArticle, KnowledgeCategory } from '@/types';
import axiosInstance from './axios';

export interface QueryKnowledgeParams {
  page?: number;
  size?: number;
  categoryId?: number;
  keyword?: string;
  tags?: string[];
}

export const knowledgeApi = {
  // 获取知识分类列表
  getCategories: async (): Promise<ApiResponse<KnowledgeCategory[]>> => {
    return axiosInstance.get('/knowledge/categories');
  },

  // 获取文章列表
  getArticles: async (params: QueryKnowledgeParams): Promise<ApiResponse<{
    total: number;
    items: KnowledgeArticle[];
  }>> => {
    return axiosInstance.get('/knowledge/articles', { params });
  },

  // 获取文章详情
  getArticleById: async (id: number): Promise<ApiResponse<KnowledgeArticle>> => {
    return axiosInstance.get(`/knowledge/articles/${id}`);
  },

  // 获取热门文章
  getHotArticles: async (limit: number = 5): Promise<ApiResponse<KnowledgeArticle[]>> => {
    return axiosInstance.get('/knowledge/articles/hot', { params: { limit } });
  },

  // 获取相关文章推荐
  getRelatedArticles: async (articleId: number, limit: number = 3): Promise<ApiResponse<KnowledgeArticle[]>> => {
    return axiosInstance.get(`/knowledge/articles/${articleId}/related`, { params: { limit } });
  },

  // 搜索文章
  searchArticles: async (keyword: string, params?: QueryKnowledgeParams): Promise<ApiResponse<{
    total: number;
    items: KnowledgeArticle[];
  }>> => {
    return axiosInstance.get('/knowledge/articles/search', {
      params: {
        keyword,
        ...params,
      },
    });
  },

  // 获取分类下的文章
  getArticlesByCategory: async (categoryId: number, params?: QueryKnowledgeParams): Promise<ApiResponse<{
    total: number;
    items: KnowledgeArticle[];
  }>> => {
    return axiosInstance.get(`/knowledge/categories/${categoryId}/articles`, {
      params: {
        ...params,
      },
    });
  },

  // 获取文章标签列表
  getTags: async (): Promise<ApiResponse<string[]>> => {
    return axiosInstance.get('/knowledge/tags');
  },

  // 根据标签获取文章
  getArticlesByTags: async (tags: string[], params?: QueryKnowledgeParams): Promise<ApiResponse<{
    total: number;
    items: KnowledgeArticle[];
  }>> => {
    return axiosInstance.get('/knowledge/articles/by-tags', {
      params: {
        tags: tags.join(','),
        ...params,
      },
    });
  },

  // 文章点赞
  likeArticle: async (articleId: number): Promise<ApiResponse<void>> => {
    return axiosInstance.post(`/knowledge/articles/${articleId}/like`);
  },

  // 文章收藏
  favoriteArticle: async (articleId: number): Promise<ApiResponse<void>> => {
    return axiosInstance.post(`/knowledge/articles/${articleId}/favorite`);
  },

  // 获取收藏的文章列表
  getFavoriteArticles: async (params?: QueryKnowledgeParams): Promise<ApiResponse<{
    total: number;
    items: KnowledgeArticle[];
  }>> => {
    return axiosInstance.get('/knowledge/articles/favorites', { params });
  },
};