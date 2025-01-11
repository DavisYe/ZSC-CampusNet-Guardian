'use client';

import React, { useState } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  Chip,
  IconButton,
  Card,
  CardContent,
  CardActionArea,
  Grid,
  Skeleton,
} from '@mui/material';
import {
  Favorite as FavoriteIcon,
  FavoriteBorder as FavoriteBorderIcon,
  ThumbUp as ThumbUpIcon,
  ThumbUpOutlined as ThumbUpOutlinedIcon,
  AccessTime as AccessTimeIcon,
  Person as PersonIcon,
  Folder as FolderIcon,
} from '@mui/icons-material';
import { useParams, useRouter } from 'next/navigation';
import { useRequest } from '@/lib/hooks/useRequest';
import { knowledgeApi } from '@/lib/api/knowledge';
import { KnowledgeArticle } from '@/types';
import dayjs from 'dayjs';

export default function KnowledgeDetailPage(): React.ReactElement {
  const params = useParams();
  const router = useRouter();
  const [isLiked, setIsLiked] = useState(false);
  const [isFavorited, setIsFavorited] = useState(false);

  const { data: article, isLoading } = useRequest<KnowledgeArticle>(
    () => knowledgeApi.getArticleById(Number(params.id)),
    {
      immediate: true,
    }
  );

  const { data: relatedArticles } = useRequest<KnowledgeArticle[]>(
    () => knowledgeApi.getRelatedArticles(Number(params.id)),
    {
      immediate: true,
    }
  );

  const handleLike = async (): Promise<void> => {
    if (!article) return;
    await knowledgeApi.likeArticle(article.id);
    setIsLiked(!isLiked);
  };

  const handleFavorite = async (): Promise<void> => {
    if (!article) return;
    await knowledgeApi.favoriteArticle(article.id);
    setIsFavorited(!isFavorited);
  };

  const handleRelatedArticleClick = (articleId: number): void => {
    router.push(`/knowledge/${articleId}`);
  };

  if (isLoading || !article) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Skeleton variant="text" height={60} sx={{ mb: 2 }} />
          <Box sx={{ display: 'flex', gap: 1, mb: 3 }}>
            <Skeleton variant="text" width={100} />
            <Skeleton variant="text" width={100} />
            <Skeleton variant="text" width={100} />
          </Box>
          <Skeleton variant="rectangular" height={400} />
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={3}>
        <Grid item xs={12} md={9}>
          <Paper sx={{ p: 3 }}>
            {/* 文章标题 */}
            <Typography variant="h4" component="h1" gutterBottom>
              {article.title}
            </Typography>

            {/* 文章元信息 */}
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 3,
                color: 'text.secondary',
                mb: 3,
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <AccessTimeIcon fontSize="small" />
                <Typography variant="body2">
                  {dayjs(article.createdAt).format('YYYY-MM-DD HH:mm')}
                </Typography>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <PersonIcon fontSize="small" />
                <Typography variant="body2">管理员</Typography>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <FolderIcon fontSize="small" />
                <Typography variant="body2">网络故障</Typography>
              </Box>
            </Box>

            {/* 标签 */}
            {article.tags && article.tags.length > 0 && (
              <Box sx={{ mb: 3 }}>
                {article.tags.map((tag) => (
                  <Chip
                    key={tag}
                    label={tag}
                    size="small"
                    color="primary"
                    variant="outlined"
                    sx={{ mr: 1 }}
                  />
                ))}
              </Box>
            )}

            {/* 文章内容 */}
            <Typography
              variant="body1"
              sx={{
                mb: 3,
                lineHeight: 1.8,
                '& p': { mb: 2 },
                '& img': { maxWidth: '100%', height: 'auto', my: 2 },
              }}
            >
              {article.content}
            </Typography>

            {/* 操作按钮 */}
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
              <IconButton onClick={handleLike} color={isLiked ? 'primary' : 'default'}>
                {isLiked ? <ThumbUpIcon /> : <ThumbUpOutlinedIcon />}
              </IconButton>
              <IconButton onClick={handleFavorite} color={isFavorited ? 'primary' : 'default'}>
                {isFavorited ? <FavoriteIcon /> : <FavoriteBorderIcon />}
              </IconButton>
            </Box>
          </Paper>
        </Grid>

        {/* 相关文章 */}
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              相关文章
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              {relatedArticles?.map((relatedArticle) => (
                <Card key={relatedArticle.id} variant="outlined">
                  <CardActionArea
                    onClick={() => handleRelatedArticleClick(relatedArticle.id)}
                  >
                    <CardContent>
                      <Typography
                        variant="subtitle2"
                        sx={{
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          display: '-webkit-box',
                          WebkitLineClamp: 2,
                          WebkitBoxOrient: 'vertical',
                        }}
                      >
                        {relatedArticle.title}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {dayjs(relatedArticle.createdAt).format('YYYY-MM-DD')}
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                </Card>
              ))}
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}