'use client';

import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import { useAuth } from '@/lib/hooks/useAuth';
import CurrentRepairOrders from '@/components/repair/CurrentRepairOrders';
import HistoryRepairOrders from '@/components/repair/HistoryRepairOrders';
import Link from 'next/link';

export default function Home() {
  const { user } = useAuth();

  if (!user) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, textAlign: 'center' }}>
        <Typography variant="h4" component="h1" gutterBottom>
          请先登录以查看报修信息
        </Typography>
        <Box sx={{ mt: 4 }}>
          <Link href="/login" passHref>
            <Button 
              variant="contained" 
              size="large"
              sx={{
                backgroundColor: 'primary.main',
                '&:hover': {
                  backgroundColor: 'primary.dark',
                },
                color: 'text.primary',
                fontWeight: 'bold',
                px: 4,
                py: 2
              }}
            >
              立即登录
            </Button>
          </Link>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        欢迎使用校园网络报修系统
      </Typography>

      <Box sx={{ mt: 4 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          当前报修
        </Typography>
        <CurrentRepairOrders />
      </Box>

      <Box sx={{ mt: 4 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          历史报修
        </Typography>
        <HistoryRepairOrders />
      </Box>
    </Container>
  );
}
