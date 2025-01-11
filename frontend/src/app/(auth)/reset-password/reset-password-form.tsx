'use client';

import { Box, Paper, Typography, TextField, Button, Alert } from '@mui/material';
import * as yup from 'yup';
import { useForm } from '@/lib/hooks/useForm';
import { authApi } from '@/lib/api/auth';
import { formRules, formPatterns } from '@/lib/hooks/useForm';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';

type ResetPasswordForm = Record<string, unknown> & {
  password: string;
  confirmPassword: string;
};

const validationSchema = yup.object({
  password: yup
    .string()
    .matches(formPatterns.password, formRules.password)
    .required(formRules.required),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref('password')], '两次输入的密码不一致')
    .required(formRules.required),
});

export default function ResetPasswordForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [token, setToken] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = searchParams.get('token');
    if (!token) {
      setError('无效的重置链接');
      return;
    }
    setToken(token);
  }, [searchParams]);

  const {
    register,
    handleSubmit,
    formState: { errors },
    isSubmitting,
    submitError,
  } = useForm<ResetPasswordForm>({
    validationSchema,
    onSubmit: async (data) => {
      if (!token) return;
      await authApi.resetPassword(token, data.password as string);
      router.push('/login');
    },
  });

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Paper
      elevation={3}
      sx={{
        p: 4,
        display: 'flex',
        flexDirection: 'column',
        gap: 3,
      }}
    >
      <Typography variant="h4" component="h1" align="center" gutterBottom>
        重置密码
      </Typography>

      <form onSubmit={handleSubmit} noValidate>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            {...register('password')}
            label="新密码"
            type="password"
            error={!!errors.password}
            helperText={errors.password?.message || '必须包含大小写字母和数字，长度8-20位'}
            fullWidth
            required
          />

          <TextField
            {...register('confirmPassword')}
            label="确认密码"
            type="password"
            error={!!errors.confirmPassword}
            helperText={errors.confirmPassword?.message}
            fullWidth
            required
          />

          {submitError && (
            <Typography color="error" variant="body2" align="center">
              {submitError}
            </Typography>
          )}

          <Button
            type="submit"
            variant="contained"
            size="large"
            disabled={isSubmitting}
            fullWidth
          >
            {isSubmitting ? '提交中...' : '重置密码'}
          </Button>
        </Box>
      </form>
    </Paper>
  );
}