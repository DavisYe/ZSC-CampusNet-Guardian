'use client';

import { Box, Container, Paper, Typography, TextField, Button, Link, Alert } from '@mui/material';
import * as yup from 'yup';
import { useForm } from '@/lib/hooks/useForm';
import { authApi } from '@/lib/api/auth';
import { formRules, formPatterns } from '@/lib/hooks/useForm';
import { useState } from 'react';

type ForgotPasswordForm = Record<string, unknown> & {
  email: string;
};

const validationSchema = yup.object({
  email: yup
    .string()
    .matches(formPatterns.email, formRules.email)
    .required(formRules.required),
});

export default function ForgotPasswordPage() {
  const [isSuccess, setIsSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    isSubmitting,
    submitError,
  } = useForm<ForgotPasswordForm>({
    validationSchema,
    onSubmit: async (data) => {
      await authApi.forgotPassword(data.email as string);
      setIsSuccess(true);
    },
  });

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          py: 4,
        }}
      >
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
            找回密码
          </Typography>

          {isSuccess ? (
            <Box sx={{ textAlign: 'center' }}>
              <Alert severity="success" sx={{ mb: 3 }}>
                重置密码链接已发送到您的邮箱，请查收
              </Alert>
              <Link href="/login" variant="body2">
                返回登录
              </Link>
            </Box>
          ) : (
            <form onSubmit={handleSubmit} noValidate>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Typography variant="body2" color="text.secondary" align="center" gutterBottom>
                  请输入您注册时使用的邮箱地址，我们将向该邮箱发送重置密码的链接
                </Typography>

                <TextField
                  {...register('email')}
                  label="邮箱地址"
                  type="email"
                  error={!!errors.email}
                  helperText={errors.email?.message}
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
                  {isSubmitting ? '发送中...' : '发送重置链接'}
                </Button>

                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                  <Link href="/login" variant="body2">
                    返回登录
                  </Link>
                </Box>
              </Box>
            </form>
          )}
        </Paper>
      </Box>
    </Container>
  );
}