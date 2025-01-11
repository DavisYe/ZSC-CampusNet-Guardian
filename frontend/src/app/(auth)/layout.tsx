'use client';

import { Box, Container, Typography } from '@mui/material';
import { useAuth } from '@/lib/hooks/useAuth';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { isAuthenticated } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated) {
      router.push('/dashboard');
    }
  }, [isAuthenticated, router]);

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        bgcolor: 'background.default',
      }}
    >
      <Container maxWidth={false} sx={{ flex: 1, py: 4 }}>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            mb: 4,
          }}
        >
          <Typography
            variant="h3"
            component="h1"
            sx={{
              fontWeight: 700,
              color: 'primary.main',
              textAlign: 'center',
              mb: 1,
            }}
          >
            校园网络报修系统
          </Typography>
          <Typography
            variant="subtitle1"
            sx={{
              color: 'text.secondary',
              textAlign: 'center',
            }}
          >
            便捷、高效的校园网络故障报修平台
          </Typography>
        </Box>

        {children}

        <Box
          component="footer"
          sx={{
            mt: 'auto',
            py: 3,
            textAlign: 'center',
          }}
        >
          <Typography variant="body2" color="text.secondary">
            © {new Date().getFullYear()} 中山学院网络维护科. 保留所有权利.
          </Typography>
        </Box>
      </Container>
    </Box>
  );
}