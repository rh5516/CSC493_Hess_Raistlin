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
	WHITE("White", 1.0f, 1.0f, 1.0f),
	GRAY("Gray", 0.7f, 0.7f, 0.7f),
	BROWN("Brown", 0.7f, 0.5f, 0.3f);
	
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
