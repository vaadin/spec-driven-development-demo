import { RouterConfigurationBuilder } from '@vaadin/hilla-file-router/runtime.js';
import Flow from 'Frontend/generated/flow/Flow';
import fileRoutes from 'Frontend/generated/file-routes';
import { lazy } from 'react';

const MovieDetailView = lazy(() => import('./views/movie/MovieDetailView'));

export const { router, routes } = new RouterConfigurationBuilder()
    .withFileRoutes(fileRoutes)
    .withReactRoutes([
        { path: '/movie/:movieId', element: <MovieDetailView />, handle: { title: 'Movie Details' } }
    ])
    .withFallback(Flow)
    .protect()
    .build();
