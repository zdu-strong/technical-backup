import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from '@/component/home/Home';
import MessagePage from '@/router/page/MessagePage';
import SignInPage from '@/router/page/SignInPage';
import SignUpPage from '@/router/page/SignUpPage';
import GitPage from '@/router/page/GitPage';
import NotFoundPage from '@/router/page/NotFoundPage';
import SuperAdminRoleManagePage from '@/router/page/SuperAdminRoleManagePage';
import SuperAdminOrganizeManagePage from '@/router/page/SuperAdminOrganizeManagePage';
import SuperAdminUserManagePage from '@/router/page/SuperAdminUserManagePage';
import LumenExchangePage from '@/router/page/LumenExchangePage';
import HomeDashboardPage from '@/router/page/HomeDashboardPage';
import ManageOrganizePage from '@/router/page/ManageOrganizePage';
import ManageRolePage from '@/router/page/ManageRolePage';
import ManageUserPage from '@/router/page/ManageUserPage';

export default (
    <BrowserRouter>
        <Routes>
            <Route index element={HomeDashboardPage} />
            <Route path="/dashboard" element={HomeDashboardPage} />
            <Route path="/chat" element={MessagePage} />
            <Route path="/sign-in" element={SignInPage} />
            <Route path="/sign-up" element={SignUpPage} />
            <Route path="/git" element={GitPage} />
            <Route path="/lumen/exchange" element={LumenExchangePage} />
            <Route path="/home" element={<Home />} />
            <Route path="/not-found" element={NotFoundPage} />
            <Route path="/manage/organize" element={ManageOrganizePage} />
            <Route path="/manage/role" element={ManageRolePage} />
            <Route path="/manage/user" element={ManageUserPage} />
            <Route path="/super-admin/manage/organize" element={SuperAdminOrganizeManagePage} />
            <Route path="/super-admin/manage/role" element={SuperAdminRoleManagePage} />
            <Route path="/super-admin/manage/user" element={SuperAdminUserManagePage} />
            <Route path="*" element={NotFoundPage} />
        </Routes>
    </BrowserRouter>
)