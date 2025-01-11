'use client';

import React from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';
import * as yup from 'yup';
import { useForm } from '@/lib/hooks/useForm';
import { formRules } from '@/lib/hooks/useForm';
import { useRouter } from 'next/navigation';
import { RepairOrderType } from '@/types';
import { repairApi } from '@/lib/api/repair';
import { useState } from 'react';
import Image from 'next/image';

interface RepairForm extends Record<string, unknown> {
  type: RepairOrderType;
  description: string;
  location: string;
  images?: FileList;
}

const validationSchema = yup.object().shape({
  type: yup
    .mixed<RepairOrderType>()
    .oneOf(Object.values(RepairOrderType))
    .required(formRules.required),
  description: yup
    .string()
    .min(10, '描述至少10个字符')
    .max(500, '描述最多500个字符')
    .required(formRules.required),
  location: yup
    .string()
    .min(5, '地点至少5个字符')
    .max(100, '地点最多100个字符')
    .required(formRules.required),
  images: yup
    .mixed()
    .test('fileList', '请选择有效的文件', function (value) {
      if (!value) return true;
      return value instanceof FileList;
    })
    .test('fileSize', '图片大小不能超过5MB', function (value) {
      if (!value || !(value instanceof FileList)) return true;
      return Array.from(value).every((file) => file.size <= 5 * 1024 * 1024);
    })
    .test('fileType', '只支持JPG、PNG格式的图片', function (value) {
      if (!value || !(value instanceof FileList)) return true;
      return Array.from(value).every((file) =>
        ['image/jpeg', 'image/png'].includes(file.type)
      );
    })
    .test('fileCount', '最多上传5张图片', function (value) {
      if (!value || !(value instanceof FileList)) return true;
      return value.length <= 5;
    }),
}) as yup.ObjectSchema<RepairForm>;

export default function CreateRepairPage(): React.ReactElement {
  const router = useRouter();
  const [previewUrls, setPreviewUrls] = useState<string[]>([]);
  const [appointmentTime, setAppointmentTime] = useState<Dayjs | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
    isSubmitting,
    submitError,
  } = useForm<RepairForm>({
    validationSchema,
    onSubmit: async (data) => {
      await repairApi.createOrder({
        type: data.type,
        description: data.description,
        location: data.location,
        appointmentTime: appointmentTime?.format('YYYY-MM-DD HH:mm:ss'),
        images: data.images ? Array.from(data.images) : undefined,
      });
      router.push('/repair/list');
    },
  });

  const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    const files = event.target.files;
    if (!files) return;

    // 清除之前的预览URL
    previewUrls.forEach((url) => URL.revokeObjectURL(url));

    // 创建新的预览URL
    const urls = Array.from(files).map((file) => URL.createObjectURL(file));
    setPreviewUrls(urls);
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ py: 4 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            故障报修
          </Typography>

          <form onSubmit={handleSubmit} noValidate>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              <FormControl error={!!errors.type} required>
                <InputLabel>故障类型</InputLabel>
                <Select
                  {...register('type')}
                  label="故障类型"
                >
                  <MenuItem value={RepairOrderType.NETWORK}>网络故障</MenuItem>
                  <MenuItem value={RepairOrderType.HARDWARE}>硬件故障</MenuItem>
                  <MenuItem value={RepairOrderType.SOFTWARE}>软件故障</MenuItem>
                  <MenuItem value={RepairOrderType.OTHER}>其他故障</MenuItem>
                </Select>
                {errors.type && (
                  <FormHelperText>{String(errors.type.message)}</FormHelperText>
                )}
              </FormControl>

              <TextField
                {...register('description')}
                label="故障描述"
                multiline
                rows={4}
                error={!!errors.description}
                helperText={String(errors.description?.message) || '请详细描述故障情况，以便我们更好地解决问题'}
                required
              />

              <TextField
                {...register('location')}
                label="故障地点"
                error={!!errors.location}
                helperText={String(errors.location?.message) || '例如：图书馆二楼阅览室'}
                required
              />

              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DateTimePicker
                  label="预约维修时间（可选）"
                  value={appointmentTime}
                  onChange={setAppointmentTime}
                  minDateTime={dayjs()}
                />
              </LocalizationProvider>

              <Box>
                <input
                  {...register('images')}
                  type="file"
                  accept="image/jpeg,image/png"
                  multiple
                  onChange={handleImageChange}
                  style={{ display: 'none' }}
                  id="image-upload"
                />
                <label htmlFor="image-upload">
                  <Button
                    variant="outlined"
                    component="span"
                    sx={{ mb: 2 }}
                  >
                    上传图片（可选）
                  </Button>
                </label>
                {errors.images && (
                  <FormHelperText error>{String(errors.images.message)}</FormHelperText>
                )}
                {previewUrls.length > 0 && (
                  <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mt: 2 }}>
                    {previewUrls.map((url, index) => (
                      <Box
                        key={index}
                        sx={{
                          width: 100,
                          height: 100,
                          position: 'relative',
                          border: '1px solid #ddd',
                          borderRadius: 1,
                          overflow: 'hidden',
                        }}
                      >
                        <Image
                          src={url}
                          alt={`预览图 ${index + 1}`}
                          fill
                          style={{ objectFit: 'cover' }}
                        />
                      </Box>
                    ))}
                  </Box>
                )}
              </Box>

              {submitError && (
                <Typography color="error" variant="body2">
                  {submitError}
                </Typography>
              )}

              <Button
                type="submit"
                variant="contained"
                size="large"
                disabled={isSubmitting}
              >
                {isSubmitting ? '提交中...' : '提交报修'}
              </Button>
            </Box>
          </form>
        </Paper>
      </Box>
    </Container>
  );
}