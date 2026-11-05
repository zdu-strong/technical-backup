import { Route, Routes } from 'react-router-dom';
import BrowserRouterOrHashRouter from "@common/router/BrowserRouterOrHashRouter";
import HomePageComponent from '@component/home/HomePageComponent';
import NotFoundPageComponent from '@/component/not-found/NotFoundPageComponent';

export default (
    <BrowserRouterOrHashRouter>
        <Routes>
            <Route index element={<HomePageComponent />} />
            <Route path="*" element={NotFoundPageComponent} />
        </Routes>
    </BrowserRouterOrHashRouter>
)