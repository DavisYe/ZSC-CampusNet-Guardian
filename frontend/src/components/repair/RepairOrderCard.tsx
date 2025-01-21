'use client';

import { RepairOrder } from '@/types';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import { format } from 'date-fns';

interface RepairOrderCardProps {
  order: RepairOrder;
}

export default function RepairOrderCard({ order }: RepairOrderCardProps) {
  return (
    <Card sx={{ mb: 2 }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
          <Typography variant="h6" component="div">
            {order.type}
          </Typography>
          <Chip label={order.status} color="primary" />
        </Box>
        
        <Typography variant="body2" color="text.secondary" gutterBottom>
          创建时间：{format(new Date(order.createdAt), 'yyyy-MM-dd HH:mm')}
        </Typography>
        
        <Typography variant="body1" sx={{ mt: 1 }}>
          {order.description}
        </Typography>
      </CardContent>
    </Card>
  );
}