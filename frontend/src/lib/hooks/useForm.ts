'use client';

import { useState } from 'react';
import { useForm as useHookForm, UseFormProps, Resolver } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { ObjectSchema } from 'yup';

export interface UseFormConfig<T extends Record<string, unknown>> extends Omit<UseFormProps<T>, 'resolver'> {
  validationSchema?: ObjectSchema<T>;
  onSubmit: (data: T) => Promise<void>;
}

export function useForm<T extends Record<string, unknown>>({
  validationSchema,
  onSubmit,
  ...formConfig
}: UseFormConfig<T>) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const form = useHookForm<T>({
    ...formConfig,
    resolver: validationSchema 
      ? (yupResolver(validationSchema) as unknown as Resolver<T>)
      : undefined,
  });

  const handleSubmit = form.handleSubmit(async (data) => {
    try {
      setIsSubmitting(true);
      setSubmitError(null);
      await onSubmit(data);
    } catch (error) {
      setSubmitError(error instanceof Error ? error.message : '提交失败，请重试');
      throw error;
    } finally {
      setIsSubmitting(false);
    }
  });

  return {
    ...form,
    handleSubmit,
    isSubmitting,
    submitError,
    setSubmitError,
  };
}

// 常用的表单验证规则
export const formRules = {
  required: '此字段为必填项',
  email: '请输入有效的邮箱地址',
  phone: '请输入有效的手机号码',
  password: '密码必须包含大小写字母和数字，长度8-20位',
  studentId: '请输入有效的学号',
  username: '用户名必须是4-16位字母、数字或下划线',
  min: (min: number) => `不能小于${min}个字符`,
  max: (max: number) => `不能超过${max}个字符`,
  integer: '请输入整数',
  number: '请输入数字',
  url: '请输入有效的URL地址',
  date: '请输入有效的日期',
  time: '请输入有效的时间',
} as const;

// 常用的正则表达式
export const formPatterns = {
  phone: /^1[3-9]\d{9}$/,
  email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  username: /^[a-zA-Z0-9_]{4,16}$/,
  password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,20}$/,
  studentId: /^\d{8,12}$/,
  integer: /^\d+$/,
  number: /^\d+(\.\d+)?$/,
  url: /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/,
  date: /^\d{4}-\d{2}-\d{2}$/,
  time: /^([01]\d|2[0-3]):([0-5]\d)$/,
} as const;

export default useForm;