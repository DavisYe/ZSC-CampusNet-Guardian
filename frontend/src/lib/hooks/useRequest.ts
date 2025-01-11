'use client';

import { useState, useCallback, useEffect } from 'react';
import { ApiResponse } from '@/types';

interface RequestOptions<T> {
  immediate?: boolean;
  initialData?: T;
  retries?: number;
  retryDelay?: number;
  cacheTime?: number;
  onSuccess?: (data: T) => void;
  onError?: (error: Error) => void;
}

interface RequestState<T> {
  data: T | null;
  error: Error | null;
  isLoading: boolean;
  isError: boolean;
}

interface CacheEntry<T> {
  data: T;
  timestamp: number;
}

const cache = new Map<string, CacheEntry<unknown>>();

export function useRequest<T>(
  requestFn: () => Promise<ApiResponse<T>>,
  options: RequestOptions<T> = {}
) {
  const {
    immediate = true,
    initialData = null,
    retries = 3,
    retryDelay = 1000,
    cacheTime = 5 * 60 * 1000, // 5分钟缓存
    onSuccess,
    onError,
  } = options;

  const [state, setState] = useState<RequestState<T>>({
    data: initialData,
    error: null,
    isLoading: false,
    isError: false,
  });

  const cacheKey = requestFn.toString();

  const executeRequest = useCallback(
    async (retryCount = 0): Promise<void> => {
      try {
        setState((prev) => ({ ...prev, isLoading: true, error: null }));

        // 检查缓存
        const cached = cache.get(cacheKey) as CacheEntry<T> | undefined;
        if (cached && Date.now() - cached.timestamp < cacheTime) {
          setState({
            data: cached.data,
            error: null,
            isLoading: false,
            isError: false,
          });
          onSuccess?.(cached.data);
          return;
        }

        const response = await requestFn();
        const data = response.data;

        // 更新缓存
        cache.set(cacheKey, { data, timestamp: Date.now() });

        setState({
          data,
          error: null,
          isLoading: false,
          isError: false,
        });

        onSuccess?.(data);
      } catch (error) {
        const isRetryable = retryCount < retries;

        if (isRetryable) {
          setTimeout(() => {
            executeRequest(retryCount + 1);
          }, retryDelay);
          return;
        }

        const errorObject = error instanceof Error ? error : new Error('请求失败');
        setState({
          data: null,
          error: errorObject,
          isLoading: false,
          isError: true,
        });

        onError?.(errorObject);
      }
    },
    [requestFn, retries, retryDelay, cacheTime, onSuccess, onError, cacheKey]
  );

  const refresh = useCallback(() => {
    cache.delete(cacheKey);
    return executeRequest();
  }, [executeRequest, cacheKey]);

  useEffect(() => {
    if (immediate) {
      executeRequest();
    }
  }, [immediate, executeRequest]);

  return {
    ...state,
    refresh,
    execute: executeRequest,
  };
}

export default useRequest;