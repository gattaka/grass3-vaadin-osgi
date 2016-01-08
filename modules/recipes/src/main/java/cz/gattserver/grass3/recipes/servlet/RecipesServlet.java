package cz.gattserver.grass3.recipes.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.recipes.facades.IRecipeFacade;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;

public class RecipesServlet extends HttpServlet {
	private static final long serialVersionUID = 7172666112080085629L;

	@Autowired
	private IRecipeFacade facade;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (facade == null) {
			SpringContextHelper.inject(this);
		}

		final OutputStream out = resp.getOutputStream();
		// Set the response type
		final PrintWriter outWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));

		outWriter.print("<html>");
		outWriter.print("<body style='font-family: sans-serif;'>");
		outWriter.print("<script>alert('test');</script>");
		outWriter.print("<h1 style='font-size: 40px;'>Recepty</h1>");
		outWriter.print("<div style='font-size: 30px;'>");
		for (RecipeDTO r : facade.getRecipes()) {
			outWriter.print("<span>" + r.getDescription() + "</span>");
		}
		outWriter.print("</div>");
		outWriter.print("</body>");
		outWriter.print("</html>");

		outWriter.flush();
		outWriter.close();
	}

}
