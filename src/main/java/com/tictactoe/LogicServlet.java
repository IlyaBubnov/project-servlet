package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet (name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //get current session
        HttpSession currentSession = req.getSession();

        //get object from session
        Field field = extractField(currentSession);

        //get the index of the cell clicked on
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);
        //check that the cell that was clicked is empty. Otherwise, do nothing and send user to the same page
        //without changing the parameters in the session.
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            requestDispatcher.forward(req, resp);
            return;
        }

        //put a cross in the cell that the user clicked on
        field.getField().put(index, Sign.CROSS);

        //checking if the cross has won after adding the user's last click
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        //get an empty field cell
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            //checking if the zero wins after adding the last zero
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }
        //if there is no empty cell and no one has won, then it is a draw
        else {
            //add a flag to the session that signals that a draw has occurred
            currentSession.setAttribute("draw", true);
            //count the list of icons
            List<Sign> data = field.getFieldData();
            //update this list in session
            currentSession.setAttribute("data", data);
            //sent redirect
            resp.sendRedirect("/index.jsp");
            return;
        }

        //read a list of icons
        List<Sign> data = field.getFieldData();

        //update field object and list of icons in session
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }
    private int getSelectedIndex (HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        if (isNumeric) {
            return Integer.parseInt(click);
        } else {
            return 0;
        }
    }

    //method checks if there are three X/O's in a row
    private boolean checkWin (HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        //Add a flag to indicate that someone has won
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            currentSession.setAttribute("winner", winner);
            //reading a list of icons
            List <Sign> data = field.getFieldData();
            //update this list in session
            currentSession.setAttribute("data", data);
            //send a redirect
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
