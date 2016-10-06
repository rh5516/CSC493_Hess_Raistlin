package utilities;
import com.badlogic.gdx.graphics.Color;

/**
 * This contains all possible values and color values for changing the
 * character skin
 * 
 * @author Raistlin Hess
 *
 */
public enum CharacterSkin
{
	DEFAULT("Default", 0.6f, 0.6f, 0.6f),
	YELLOW("Yellow", 0.8f, 0.4f, 0.8f),
	RED("Red", 1.0f, 0.4f, 0.4f);
	
	private String name;
	private Color color = new Color();
	
	private CharacterSkin(String name, float r, float g, float b)
	{
		this.name = name;
		color.set(r,g,b,1.0f);
	}
	
	/**
	 * Returns the name variable of this object
	 */
	@Override
	public String toString()
	{
		return name;
	}
	
	/**
	 * Getter for color
	 */
	public Color getColor()
	{
		return color;
	}
}

