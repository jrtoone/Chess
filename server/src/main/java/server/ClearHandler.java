package server;

import service.AdminService;
import io.javalin.http.Context;
import dataaccess.DataAccessException;

public class ClearHandler {
    private final AdminService adminService;
    public ClearHandler(AdminService adminService) {
        this.adminService = adminService;
    }

    public void clear(Context ctx) throws DataAccessException {
        adminService.clearApp();
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }
}
