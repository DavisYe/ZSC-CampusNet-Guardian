'use client';

import React from 'react';
import {
  Box,
  Container,
  Grid,
  Paper,
  Typography,
  Button,
  Card,
  CardContent,
  CardActions,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
} from '@mui/material';
import {
  Add as AddIcon,
  Build as BuildIcon,
  Article as ArticleIcon,
  Schedule as ScheduleIcon,
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
} from '@mui/icons-material';
import { useRouter } from 'next/navigation';
import { useRequest } from '@/lib/hooks/useRequest';
import { repairApi } from '@/lib/api/repair';
import { RepairOrder, RepairOrderStatus } from '@/types';
import dayjs from 'dayjs';

interface OrderStats {
  total: number;
  pending: number;
  processing: number;
  completed: number;
}

interface OrderListResponse {
  total: number;
  items: RepairOrder[];
}

const statusColors: Record<RepairOrderStatus, 'warning' | 'info' | 'error' | 'success'> = {
  [RepairOrderStatus.PENDING]: 'warning',
  [RepairOrderStatus.PROCESSING]: 'info',
  [RepairOrderStatus.ESCALATED]: 'error',
  [RepairOrderStatus.COMPLETED]: 'success',
} as const;

const statusLabels: Record<RepairOrderStatus, string> = {
  [RepairOrderStatus.PENDING]: '待处理',
  [RepairOrderStatus.PROCESSING]: '处理中',
  [RepairOrderStatus.ESCALATED]: '已上报',
  [RepairOrderStatus.COMPLETED]: '已完成',
} as const;

const quickActions = [
  {
    title: '提交报修',
    description: '提交新的故障报修工单',
    icon: <AddIcon />,
    path: '/repair/create',
    color: '#1976d2',
  },
  {
    title: '报修记录',
    description: '查看我的报修历史记录',
    icon: <BuildIcon />,
    path: '/repair/list',
    color: '#2e7d32',
  },
  {
    title: '故障知识库',
    description: '浏览常见故障解决方案',
    icon: <ArticleIcon />,
    path: '/knowledge',
    color: '#ed6c02',
  },
];

export default function DashboardPage(): React.ReactElement {
  const router = useRouter();

  const { data: stats } = useRequest<OrderStats>(
    () => repairApi.getMyOrderStats(),
    {
      immediate: true,
    }
  );

  const { data: recentOrders } = useRequest<OrderListResponse>(
    () =>
      repairApi.getOrders({
        page: 1,
        size: 5,
      }),
    {
      immediate: true,
    }
  );

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={3}>
        {/* 统计卡片 */}
        <Grid item xs={12}>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6} md={3}>
              <Paper
                sx={{
                  p: 2,
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                }}
              >
                <Typography variant="h4" color="primary">
                  {stats?.total || 0}
                </Typography>
                <Typography color="text.secondary">总工单数</Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Paper
                sx={{
                  p: 2,
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                }}
              >
                <Typography variant="h4" color="warning.main">
                  {stats?.pending || 0}
                </Typography>
                <Typography color="text.secondary">待处理</Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Paper
                sx={{
                  p: 2,
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                }}
              >
                <Typography variant="h4" color="info.main">
                  {stats?.processing || 0}
                </Typography>
                <Typography color="text.secondary">处理中</Typography>
              </Paper>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Paper
                sx={{
                  p: 2,
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                }}
              >
                <Typography variant="h4" color="success.main">
                  {stats?.completed || 0}
                </Typography>
                <Typography color="text.secondary">已完成</Typography>
              </Paper>
            </Grid>
          </Grid>
        </Grid>

        {/* 快速操作 */}
        <Grid item xs={12}>
          <Typography variant="h6" gutterBottom>
            快速操作
          </Typography>
          <Grid container spacing={3}>
            {quickActions.map((action) => (
              <Grid item xs={12} sm={6} md={4} key={action.title}>
                <Card>
                  <CardContent>
                    <Box
                      sx={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 2,
                        mb: 1,
                      }}
                    >
                      <Box
                        sx={{
                          width: 40,
                          height: 40,
                          borderRadius: 1,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          bgcolor: action.color,
                          color: 'white',
                        }}
                      >
                        {action.icon}
                      </Box>
                      <Typography variant="h6">{action.title}</Typography>
                    </Box>
                    <Typography variant="body2" color="text.secondary">
                      {action.description}
                    </Typography>
                  </CardContent>
                  <CardActions>
                    <Button
                      size="small"
                      onClick={() => router.push(action.path)}
                    >
                      立即前往
                    </Button>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Grid>

        {/* 最近工单 */}
        <Grid item xs={12}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              最近工单
            </Typography>
            <List>
              {recentOrders?.items.map((order: RepairOrder, index: number) => (
                <React.Fragment key={order.id}>
                  {index > 0 && <Divider />}
                  <ListItem
                    secondaryAction={
                      <Chip
                        label={statusLabels[order.status]}
                        color={statusColors[order.status]}
                        size="small"
                      />
                    }
                  >
                    <ListItemIcon>
                      {order.status === RepairOrderStatus.COMPLETED ? (
                        <CheckCircleIcon color="success" />
                      ) : order.status === RepairOrderStatus.ESCALATED ? (
                        <ErrorIcon color="error" />
                      ) : (
                        <ScheduleIcon color="action" />
                      )}
                    </ListItemIcon>
                    <ListItemText
                      primary={order.description}
                      secondary={dayjs(order.createdAt).format(
                        'YYYY-MM-DD HH:mm'
                      )}
                      primaryTypographyProps={{
                        sx: {
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap',
                        },
                      }}
                    />
                  </ListItem>
                </React.Fragment>
              ))}
            </List>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}