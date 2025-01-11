'use client';

import React, { useState } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  Grid,
  Chip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Rating,
  TextField,
} from '@mui/material';
import {
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineOppositeContent,
} from '@mui/lab';
import {
  CheckCircle as CheckCircleIcon,
  Schedule as ScheduleIcon,
  Error as ErrorIcon,
  Build as BuildIcon,
} from '@mui/icons-material';
import { useParams, useRouter } from 'next/navigation';
import { useRequest } from '@/lib/hooks/useRequest';
import { repairApi } from '@/lib/api/repair';
import { RepairOrder, RepairOrderStatus, RepairOrderType } from '@/types';
import dayjs from 'dayjs';
import Image from 'next/image';

const statusColors: Record<RepairOrderStatus, 'warning' | 'info' | 'error' | 'success'> = {
  [RepairOrderStatus.PENDING]: 'warning',
  [RepairOrderStatus.PROCESSING]: 'info',
  [RepairOrderStatus.ESCALATED]: 'error',
  [RepairOrderStatus.COMPLETED]: 'success',
};

const statusLabels: Record<RepairOrderStatus, string> = {
  [RepairOrderStatus.PENDING]: '待处理',
  [RepairOrderStatus.PROCESSING]: '处理中',
  [RepairOrderStatus.ESCALATED]: '已上报',
  [RepairOrderStatus.COMPLETED]: '已完成',
};

const typeLabels: Record<RepairOrderType, string> = {
  [RepairOrderType.NETWORK]: '网络故障',
  [RepairOrderType.HARDWARE]: '硬件故障',
  [RepairOrderType.SOFTWARE]: '软件故障',
  [RepairOrderType.OTHER]: '其他故障',
};

const statusIcons: Record<RepairOrderStatus, React.ReactNode> = {
  [RepairOrderStatus.PENDING]: <ScheduleIcon color="warning" />,
  [RepairOrderStatus.PROCESSING]: <BuildIcon color="info" />,
  [RepairOrderStatus.ESCALATED]: <ErrorIcon color="error" />,
  [RepairOrderStatus.COMPLETED]: <CheckCircleIcon color="success" />,
};

export default function RepairDetailPage(): React.ReactElement {
  const params = useParams();
  const router = useRouter();
  const [ratingOpen, setRatingOpen] = useState(false);
  const [rating, setRating] = useState<number | null>(0);
  const [comment, setComment] = useState('');
  const [selectedImage, setSelectedImage] = useState<string | null>(null);

  const { data: order, isLoading, error } = useRequest<RepairOrder>(
    () => repairApi.getOrderById(Number(params.id)),
    {
      immediate: true,
    }
  );

  const handleRatingSubmit = async (): Promise<void> => {
    if (!rating || !order) return;
    await repairApi.rateOrder(order.id, rating, comment);
    setRatingOpen(false);
    router.refresh();
  };

  if (error) {
    return (
      <Container maxWidth="md">
        <Box sx={{ py: 4 }}>
          <Typography color="error">加载失败: {error.message}</Typography>
        </Box>
      </Container>
    );
  }

  if (isLoading || !order) {
    return (
      <Container maxWidth="md">
        <Box sx={{ py: 4 }}>
          <Typography>加载中...</Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Box sx={{ py: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Box sx={{ mb: 4 }}>
            <Typography variant="h5" component="h1" gutterBottom>
              工单详情 #{order.id}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" color="text.secondary">
                  故障类型
                </Typography>
                <Typography>{typeLabels[order.type]}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" color="text.secondary">
                  当前状态
                </Typography>
                <Chip
                  label={statusLabels[order.status]}
                  color={statusColors[order.status]}
                  size="small"
                />
              </Grid>
              <Grid item xs={12}>
                <Typography variant="subtitle2" color="text.secondary">
                  故障地点
                </Typography>
                <Typography>{order.location}</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography variant="subtitle2" color="text.secondary">
                  故障描述
                </Typography>
                <Typography>{order.description}</Typography>
              </Grid>
              {order.appointmentTime && (
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">
                    预约时间
                  </Typography>
                  <Typography>
                    {dayjs(order.appointmentTime).format('YYYY-MM-DD HH:mm')}
                  </Typography>
                </Grid>
              )}
            </Grid>
          </Box>

          {order.images && order.images.length > 0 && (
            <Box sx={{ mb: 4 }}>
              <Typography variant="h6" gutterBottom>
                故障图片
              </Typography>
              <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                {order.images.map((url, index) => (
                  <Box
                    key={index}
                    sx={{
                      width: 100,
                      height: 100,
                      position: 'relative',
                      cursor: 'pointer',
                      border: '1px solid #ddd',
                      borderRadius: 1,
                      overflow: 'hidden',
                    }}
                    onClick={() => setSelectedImage(url)}
                  >
                    <Image
                      src={url}
                      alt={`故障图片 ${index + 1}`}
                      fill
                      style={{ objectFit: 'cover' }}
                    />
                  </Box>
                ))}
              </Box>
            </Box>
          )}

          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" gutterBottom>
              处理进度
            </Typography>
            <Timeline>
              {order.timeline?.map((event, index) => (
                <TimelineItem key={index}>
                  <TimelineOppositeContent color="text.secondary">
                    {dayjs(event.timestamp).format('YYYY-MM-DD HH:mm')}
                  </TimelineOppositeContent>
                  <TimelineSeparator>
                    <TimelineDot>{statusIcons[event.status]}</TimelineDot>
                    {index < order.timeline.length - 1 && <TimelineConnector />}
                  </TimelineSeparator>
                  <TimelineContent>
                    <Typography>{event.description}</Typography>
                    {event.operator && (
                      <Typography variant="body2" color="text.secondary">
                        处理人: {event.operator}
                      </Typography>
                    )}
                  </TimelineContent>
                </TimelineItem>
              ))}
            </Timeline>
          </Box>

          {order.status === RepairOrderStatus.COMPLETED && !order.rating && (
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
              <Button variant="contained" onClick={() => setRatingOpen(true)}>
                评价维修服务
              </Button>
            </Box>
          )}
        </Paper>
      </Box>

      <Dialog open={ratingOpen} onClose={() => setRatingOpen(false)}>
        <DialogTitle>评价维修服务</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Typography>满意度：</Typography>
              <Rating
                value={rating}
                onChange={(_, value) => setRating(value)}
                size="large"
              />
            </Box>
            <TextField
              label="评价内容"
              multiline
              rows={4}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              fullWidth
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRatingOpen(false)}>取消</Button>
          <Button onClick={handleRatingSubmit} disabled={!rating}>
            提交评价
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={!!selectedImage}
        onClose={() => setSelectedImage(null)}
        maxWidth="lg"
        fullWidth
      >
        <DialogContent>
          {selectedImage && (
            <Box
              sx={{
                position: 'relative',
                width: '100%',
                height: '80vh',
              }}
            >
              <Image
                src={selectedImage}
                alt="故障图片预览"
                fill
                style={{ objectFit: 'contain' }}
              />
            </Box>
          )}
        </DialogContent>
      </Dialog>
    </Container>
  );
}