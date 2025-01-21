'use client';

import { useEffect, useState } from 'react';
import { repairApi } from '@/lib/api/repair';
import { useAuth } from '@/lib/hooks/useAuth';
import RepairOrderCard from '@/components/repair/RepairOrderCard';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import { RepairOrder } from '@/types';

interface ApiError {
  message: string;
}

export default function CurrentRepairOrders() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [orders, setOrders] = useState<RepairOrder[]>([]);

  useEffect(() => {
    if (user) {
      const fetchOrders = async () => {
        try {
          const res = await repairApi.getOrders({ status: 'pending,processing' });
          setOrders(res.data.items);
        } catch (err) {
          const error = err as ApiError;
          setError(error.message || '获取当前报修信息失败');
        } finally {
          setLoading(false);
        }
      };

      fetchOrders();
    }
  }, [user]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <>
      {orders.length > 0 ? (
        orders.map(order => (
          <RepairOrderCard key={order.id} order={order} />
        ))
      ) : (
        <Typography variant="body1">暂无进行中的报修</Typography>
      )}
    </>
  );
}