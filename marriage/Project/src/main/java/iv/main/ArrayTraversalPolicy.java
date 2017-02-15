package iv.main;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;

public class ArrayTraversalPolicy extends FocusTraversalPolicy
{
	private int defaultComponent = -1;
	private ArrayList<Component> components = new ArrayList<>();

	public void addAndSetDefault(Component component)
	{
		defaultComponent = components.size();
		add(component);
	}
	public void add(Component component)
	{
		components.add(component);
	}
	
	private int getIndex(Component component)
	{
		for (int i = 0; i < components.size(); i++)
			if (components.get(i).equals(component))
				return i;
		return -1;
	}
	
	private int next(int current, int direction)
	{
		current += direction;
		while (current < 0)
			current += components.size();
		while (current >= components.size())
			current -= components.size();
		return current;
	}
	
	private Component getNext(int current, int searchDirection)
	{
		for (int i = next(current, searchDirection), count = 0; count < components.size() ; count++, i = next(i, searchDirection))
		{
			Component component = components.get(i);
			if (component.isEnabled())
				return component;
		}
		return null;
	}
	
	@Override
	public Component getComponentAfter(
			Container aContainer,
			Component aComponent) {
		int index = getIndex(aComponent);
		if (index < 0)
			return getDefaultComponent();
		return getNext(index, 1);
	}

	@Override
	public Component getComponentBefore(
			Container aContainer,
			Component aComponent) {
		int index = getIndex(aComponent);
		if (index < 0)
			return getDefaultComponent();
		return getNext(index, -1);
	}
	
	// what if it is not enabled...
	public Component getDefaultComponent()
	{
		return getNext(defaultComponent >= 0 ? defaultComponent : 0, 1);
	}

	@Override
	public Component getDefaultComponent(Container aContainer) {
		return getDefaultComponent();
	}

	@Override
	public Component getFirstComponent(Container aContainer) {
		return components.isEmpty() ? null : components.get(0);
	}

	@Override
	public Component getLastComponent(Container aContainer) {
		return components.isEmpty() ? null : components.get(components.size() - 1);
	}
}
