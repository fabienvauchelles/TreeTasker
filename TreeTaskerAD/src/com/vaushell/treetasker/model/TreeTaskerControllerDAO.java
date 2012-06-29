package com.vaushell.treetasker.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TreeTaskerControllerDAO
{
	public static List<TT_Task> getMockTaskList1()
	{
		List<TT_Task> mockList = new ArrayList<TT_Task>();

		TT_Task construireMaison = new TT_Task( UUID.randomUUID().toString(),
			"Construire la maison", new Date(), TT_Task.TODO );

		construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter terrain", new Date(), TT_Task.DONE ) );
		
		TT_Task acheterMatos = new TT_Task(UUID.randomUUID().toString(), "Acheter matériel", new Date(), TT_Task.TODO  );
		acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter brique", new Date(), TT_Task.TODO ) );
		acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter brouette", new Date(), TT_Task.TODO ) );
		construireMaison.addChildTask( acheterMatos );
		
		construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Construire les murs", new Date(), TT_Task.TODO ) );
		construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Construire le toit", new Date(), TT_Task.TODO ) );

		TT_Task passerPermis = new TT_Task( UUID.randomUUID().toString(),
			"Passer le permis", new Date(), TT_Task.TODO );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(), "S'inscrire à l'auto-école", new Date(), TT_Task.DONE ) );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Obtenir le code", new Date(), TT_Task.TODO ) );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Faire 20h de conduite", new Date(), TT_Task.TODO ) );
		
		TT_Task accederAuLevelDesPoneys = new TT_Task( UUID.randomUUID().toString(),
			"Accéder au niveau caché", new Date(), TT_Task.TODO );
		
		TT_Task rassemblerIngredients = new TT_Task(UUID.randomUUID().toString(), "Trouver les ingrédients", new Date(), TT_Task.TODO  );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver champignon noir", new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver tibia de léoric", new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver arc-en-ciel liquide", new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver pierre baragouinante", new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter cloche de Wirt", new Date(), TT_Task.TODO ) );
		accederAuLevelDesPoneys.addChildTask( rassemblerIngredients );
		
		accederAuLevelDesPoneys.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Construire le baton de bouvier", new Date(), TT_Task.DONE ) );
		accederAuLevelDesPoneys.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Ouvrir le portail", new Date(), TT_Task.TODO ) );

		mockList.add( construireMaison );
		mockList.add( passerPermis );
		mockList.add( accederAuLevelDesPoneys );
		
		return mockList;
	}
}
