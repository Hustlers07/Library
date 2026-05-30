import { RenderMode, ServerRoute } from '@angular/ssr';
import { ROUTES } from './constants/api.constants';

export const serverRoutes: ServerRoute[] = [

  {
    // Dynamic route — render on the server per-request instead of prerendering
    // If your ROUTES.PROFILE resolves to 'user/:uid', use that exact path here
    path: ROUTES.PROFILE.slice(1),
    renderMode: RenderMode.Server,
  },
  {
    // Catch-all — server-render anything else
    path: '**',
    renderMode: RenderMode.Prerender,
  },

];