import { RenderMode, ServerRoute } from '@angular/ssr';
import { ROUTES } from './constants/api.constants';

export const serverRoutes: ServerRoute[] = [
  {
    // Static routes — safe to prerender
    path: '',
    renderMode: RenderMode.Prerender,
  },
  {
    path: ROUTES.LOGIN.slice(1),
    renderMode: RenderMode.Prerender,
  },
  {
    path: ROUTES.REGISTER.slice(1),
    renderMode: RenderMode.Prerender,
  },
  {
    path: ROUTES.RESET_PASSWORD.slice(1),
    renderMode: RenderMode.Prerender,
  },
  {
    // Dynamic route — render on the server per-request instead of prerendering
    // If your ROUTES.PROFILE resolves to 'user/:uid', use that exact path here
    path: ROUTES.PROFILE.slice(1),
    renderMode: RenderMode.Server,
  },
  {
    // Dashboard likely requires auth — no point prerendering
    path: ROUTES.DASHBOARD.slice(1),
    renderMode: RenderMode.Server,
  },
  {
    // Dashboard likely requires auth — no point prerendering
    path: ROUTES.ROOM.slice(1),
    renderMode: RenderMode.Server,
  },
  {
    // Catch-all — server-render anything else
    path: '**',
    renderMode: RenderMode.Server,
  },
];