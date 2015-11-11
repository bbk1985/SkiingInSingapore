package com.ben;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	private static int singaporeMountain[][];
	private static int mapX = 0;
	private static int mapY = 0;
	
	private static ArrayList<Point> currentRoute = new ArrayList<Point>();
	private static ArrayList<ArrayList<Point>> allSkiRoute = new ArrayList<ArrayList<Point>>();

	private static StringBuilder sb = new StringBuilder();
	
	public static void main(String[] args) {
		
		boolean getMapSize = false;
		int rowCounter = 0;
		int colCounter = 0;
		int height = 0;
		Scanner scanner = null;
		
		try {
			scanner = new Scanner(new File(args[0]));
			
			// read file
			while (scanner.hasNext()) {
				// get first 2 values from file as map parameters
				if (!getMapSize) {
					mapX = Integer.valueOf(scanner.next());
					mapY = Integer.valueOf(scanner.next());
					getMapSize=true;
					
					singaporeMountain = new int[mapX][mapY];
				}
				
				// start reading height values
				height = Integer.valueOf(scanner.next());

				// read from file is left to right, top to bottom
				singaporeMountain[rowCounter][colCounter] = height;

				colCounter++;
				
				// if column count reaches map limit, change row
				if (colCounter%mapX==0) {
					rowCounter++;
					colCounter=0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner!=null)
				scanner.close();
		}

		// ski for longest route, starting with top left corner of map
		Point startPoint = null;
		for (int y=0; y<mapY; y++) {
			for (int x=0; x<mapX; x++) {
				startPoint = new Point(x,y);
				currentRoute = new ArrayList<Point>();
				getNextRoute(startPoint);
			}
		}
		
		// sort recorded route in descending order
		allSkiRoute.sort(new Comparator<ArrayList<Point>>() {
			@Override
			public int compare(ArrayList<Point> o1, ArrayList<Point> o2) {
				// TODO Auto-generated method stub
				return o2.size()-o1.size();
			}
		});
		
		int prevListSize = 0;
		int prevDropDiff = 0;
		int currListSize = 0;
		int currDropDiff = 0;
		
		Point currStartPoint = null;
		Point currEndPoint = null;
		
		// compare longest route and depth
		List<Point> bestRoute = new ArrayList<Point>();
		for (List<Point> skiPoints : allSkiRoute) {
		
			currListSize = skiPoints.size();
			currStartPoint = skiPoints.get(0);
			currEndPoint = skiPoints.get(skiPoints.size()-1);
			currDropDiff = singaporeMountain[currStartPoint.y][currStartPoint.x]
							-singaporeMountain[currEndPoint.y][currEndPoint.x];

			for (Point p : skiPoints) {
				sb.append(singaporeMountain[p.y][p.x] + " ");
			}
			sb.append(" : "+currDropDiff);
			System.out.println(skiPoints.size() + " > " + sb.toString());
			sb.setLength(0);
			
			// if is first ski attempt
			if (prevListSize==0) {
				prevListSize = currListSize;
				prevDropDiff = currDropDiff;
				bestRoute = skiPoints;
				continue;
			}

			// exit loop if current route is shorter than highest recorded
			if (currListSize < prevListSize) {
				break;
			}
			
			// if route length is same, compare drop height
			if ((currListSize == prevListSize) && (currDropDiff > prevDropDiff)) {
				prevDropDiff = currDropDiff;
				prevListSize = currListSize;
				bestRoute = skiPoints;
			}
		}
		
		for (Point p : bestRoute) {
			sb.append(singaporeMountain[p.y][p.x]);
		}
		sb.append(prevDropDiff);
		System.out.println("best route > " + sb.toString());
	}
	
	// iterates through all available ski points around input point
	private static Point getNextRoute(Point entryPoint) {

		// store current route
		currentRoute.add(entryPoint);
		
		// check for available route
		List<Point> tryPoints = surroundingPoints(entryPoint);
		if (tryPoints.size()==0) {
			// record this dead end route in master list
			allSkiRoute.add(new ArrayList<Point>(currentRoute));
			return null;
		} else {
			// iterate through possible points until null
			Point result = null;
			for (Point tryPoint : tryPoints) {
				while (true) {
					result = getNextRoute(tryPoint);
					if (null==result) {
						// backtrack one point from traversed tree
						currentRoute.remove(currentRoute.size()-1);
						break;
					}
				}
			}
		}
		
		return null;
	}
	
	// validates and returns points that passes criteria
	private static List<Point> surroundingPoints(Point curPoint) {
		
		List<Point> goodPoints = new ArrayList<Point>();
		
		// is left ok (position, height, used)
		if (curPoint.x-1>=0)
			if (!currentRoute.contains(new Point(curPoint.x-1, curPoint.y)))
				if (singaporeMountain[curPoint.y][curPoint.x-1] < singaporeMountain[curPoint.y][curPoint.x]) 
					goodPoints.add(new Point(curPoint.x-1, curPoint.y));
		
		// is right ok (position, height, used)
		if (curPoint.x+1<mapX)
			if (!currentRoute.contains(new Point(curPoint.x+1, curPoint.y)))
				if (singaporeMountain[curPoint.y][curPoint.x+1] < singaporeMountain[curPoint.y][curPoint.x]) 
					goodPoints.add(new Point(curPoint.x+1, curPoint.y));
		
		// is up ok (position, height, used)
		if (curPoint.y-1>=0)
			if (!currentRoute.contains(new Point(curPoint.x, curPoint.y-1)))
				if (singaporeMountain[curPoint.y-1][curPoint.x] < singaporeMountain[curPoint.y][curPoint.x]) 
					goodPoints.add(new Point(curPoint.x, curPoint.y-1));
		
		// is down ok (position, height, used)
		if (curPoint.y+1<mapY)
			if (!currentRoute.contains(new Point(curPoint.x, curPoint.y+1)))
				if (singaporeMountain[curPoint.y+1][curPoint.x] < singaporeMountain[curPoint.y][curPoint.x]) 
					goodPoints.add(new Point(curPoint.x, curPoint.y+1));
		
		return goodPoints;
	}
}
