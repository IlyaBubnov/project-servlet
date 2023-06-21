package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //create new session
        HttpSession currentSession = req.getSession(true);

        //create new game field
        Field field = new Field();
        Map <Integer, Sign> fieldData = field.getField();

        //Get list of field values
        List <Sign> data = field.getFieldData();

        //Add field parameters to the session (will be needed to store state between requests)
        currentSession.setAttribute("field", field);
        //Add field values sorted by index to the session (necessary for drawing crosses and zeroes)
        currentSession.setAttribute("data", data);

        //Redirecting request to index.jsp page via server
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
