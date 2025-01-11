'use client';

import { Box, Container, Paper, Typography, TextField, Button, Link } from '@mui/material';
import * as yup from 'yup';
import { useForm } from '@/lib/hooks/useForm';
import { useAuth } from '@/lib/hooks/useAuth';
import { formRules, formPatterns } from '@/lib/hooks/useForm';
import { useRouter } from 'next/navigation';

type RegisterForm = Record<string, unknown> & {
  username: string;
  password: string;
  studentId: string;
  email?: string;
  phone?: string;
};

const validationSchema = yup.object({
  username: yup
    .string()
    .matches(formPatterns.username, formRules.username)
    .required(formRules.required),
  password: yup
    .string()
    .matches(formPatterns.password, formRules.password)
    .required(formRules.required),
  studentId: yup
    .string()
    .matches(formPatterns.studentId, formRules.studentId)
    .required(formRules.required),
  email: yup
    .string()
    .matches(formPatterns.email, formRules.email)
    .optional(),
  phone: yup
    .string()
    .matches(formPatterns.phone, formRules.phone)
    .optional(),
});

export default function RegisterPage() {
  const router = useRouter();
  const { register: authRegister } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
    isSubmitting,
    submitError,
  } = useForm<RegisterForm>({
    validationSchema,
    onSubmit: async (data) => {
      await authRegister({
        username: data.username as string,
        password: data.password as string,
        studentId: data.studentId as string,
        email: data.email as string | undefined,
        phone: data.phone as string | undefined,
      });
      router.push('/login');
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
            注册
          </Typography>

          <form onSubmit={handleSubmit} noValidate>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <TextField
                {...register('username')}
                label="用户名"
                error={!!errors.username}
                helperText={errors.username?.message || '4-16位字母、数字或下划线'}
                fullWidth
                required
              />

              <TextField
                {...register('password')}
                label="密码"
                type="password"
                error={!!errors.password}
                helperText={errors.password?.message || '必须包含大小写字母和数字，长度8-20位'}
                fullWidth
                required
              />

              <TextField
                {...register('studentId')}
                label="学号"
                error={!!errors.studentId}
                helperText={errors.studentId?.message || '8-12位数字'}
                fullWidth
                required
              />

              <TextField
                {...register('email')}
                label="邮箱（可选）"
                type="email"
                error={!!errors.email}
                helperText={errors.email?.message}
                fullWidth
              />

              <TextField
                {...register('phone')}
                label="手机号（可选）"
                error={!!errors.phone}
                helperText={errors.phone?.message}
                fullWidth
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
                {isSubmitting ? '注册中...' : '注册'}
              </Button>

              <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                <Link href="/login" variant="body2">
                  已有账号？立即登录
                </Link>
              </Box>
            </Box>
          </form>
        </Paper>
      </Box>
    </Container>
  );
}