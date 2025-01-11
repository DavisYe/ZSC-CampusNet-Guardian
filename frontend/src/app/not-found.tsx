'use client';

import { Box, Container, Typography, Button } from '@mui/material';
import { useRouter } from 'next/navigation';

export default function NotFound() {
  const router = useRouter();

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          gap: 2,
        }}
      >
        <Typography variant="h1" component="h1" align="center">
          404
        </Typography>
        <Typography variant="h4" component="h2" align="center">
          页面未找到
        </Typography>
        <Typography variant="body1" align="center" color="text.secondary">
          抱歉，您访问的页面不存在。
        </Typography>
        <Button
          variant="contained"
          onClick={() => router.push('/')}
          sx={{ mt: 2 }}
        >
          返回首页
        </Button>
      </Box>
    </Container>
  );
}