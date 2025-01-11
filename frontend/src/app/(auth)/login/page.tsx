'use client';

import { Box, Container, Paper, Typography, TextField, Button, Link } from '@mui/material';
import * as yup from 'yup';
import { useForm } from '@/lib/hooks/useForm';
import { useAuth } from '@/lib/hooks/useAuth';
import { formRules, formPatterns } from '@/lib/hooks/useForm';
import { useRouter } from 'next/navigation';

type LoginForm = Record<string, unknown> & {
  username: string;
  password: string;
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
});

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();

  const {
    register,
    handleSubmit,
    formState: { errors },
    isSubmitting,
    submitError,
  } = useForm<LoginForm>({
    validationSchema,
    onSubmit: async (data) => {
      await login(data.username as string, data.password as string);
      router.push('/dashboard');
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
            登录
          </Typography>

          <form onSubmit={handleSubmit} noValidate>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <TextField
                {...register('username')}
                label="用户名"
                error={!!errors.username}
                helperText={errors.username?.message}
                fullWidth
                required
              />

              <TextField
                {...register('password')}
                label="密码"
                type="password"
                error={!!errors.password}
                helperText={errors.password?.message}
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
                {isSubmitting ? '登录中...' : '登录'}
              </Button>

              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
                <Link href="/register" variant="body2">
                  注册账号
                </Link>
                <Link href="/forgot-password" variant="body2">
                  忘记密码？
                </Link>
              </Box>
            </Box>
          </form>
        </Paper>
      </Box>
    </Container>
  );
}