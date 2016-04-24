/*	HTL Leonding	*/
package at.plakolb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author Andreas
 */
public class ModifyController extends Observable {

    private static ModifyController instance;

    private ModifyController() {
    }

    public static ModifyController getInstance() {
        if (instance == null) {
            instance = new ModifyController();
        }
        return instance;
    }
    private Boolean project_information = false;
    private Boolean project_resultArea = false;
    private Boolean project_constructionmaterialList = false;
    private Boolean assembling = false;
    private Boolean project_constructionMaterial = false;
    private Boolean project_colour = false;
    private Boolean project_transport = false;
    private Boolean project_materialAndCost = false;

    //Assembling-Unterreiter
    private Boolean assembling_formwork = false;
    private Boolean assembling_foil = false;
    private Boolean assembling_visibleFormwork = false;
    private Boolean assembling_sealingBand = false;
    private Boolean assembling_counterBattens = false;
    private Boolean assembling_battensOrFullFormwork = false;

    public void reset() {
        project_information = false;
        project_resultArea = false;
        project_constructionmaterialList = false;
        assembling = false;
        project_constructionMaterial = false;
        project_colour = false;
        project_transport = false;
        project_materialAndCost = false;

        assembling_formwork = false;
        assembling_foil = false;
        assembling_visibleFormwork = false;
        assembling_sealingBand = false;
        assembling_counterBattens = false;
        assembling_battensOrFullFormwork = false;
    }

    public List<Boolean> getModifiedList() {
        List<Boolean> res = new ArrayList<>();

        res.add(project_information);
        res.add(project_resultArea);
        res.add(project_constructionmaterialList);
        res.add(assembling);
        res.add(project_constructionMaterial);
        res.add(project_colour);
        res.add(project_transport);
        res.add(project_materialAndCost);
        return res;
    }
    public List<Boolean> getAssemblingModifiedList() {
        List<Boolean> res = new ArrayList<>();

        res.add(assembling_formwork);
        res.add(assembling_foil);
        res.add(assembling_visibleFormwork);
        res.add(assembling_sealingBand);
        res.add(assembling_counterBattens);
        res.add(assembling_battensOrFullFormwork);
        return res;
    }

    public boolean isModified() {
        for (Boolean b : getModifiedList()) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    public void setProject_information(Boolean project_information) {
        this.project_information = project_information;
        setChanged();
        notifyObservers();
    }

    public void setProject_resultArea(Boolean project_resultArea) {
        this.project_resultArea = project_resultArea;
        setChanged();
        notifyObservers();
    }

    public void setProject_constructionmaterialList(Boolean project_constructionmaterialList) {
        this.project_constructionmaterialList = project_constructionmaterialList;
        setChanged();
        notifyObservers();
    }

    public void setAssembling(Boolean assembling) {
        this.assembling = assembling;
        setChanged();
        notifyObservers();
    }

    public void setProject_constructionMaterial(Boolean project_constructionMaterial) {
        this.project_constructionMaterial = project_constructionMaterial;
        setChanged();
        notifyObservers();
    }

    public void setProject_colour(Boolean project_colour) {
        this.project_colour = project_colour;
        setChanged();
        notifyObservers();
    }

    public void setProject_transport(Boolean project_transport) {
        this.project_transport = project_transport;
        setChanged();
        notifyObservers();
    }

    public void setProject_materialAndCost(Boolean project_materialAndCost) {
        this.project_materialAndCost = project_materialAndCost;
        setChanged();
        notifyObservers();
    }
 
    public void setAssembling_formwork(Boolean assembling_formwork) {
        this.assembling_formwork = assembling_formwork;
        setAssembling(true);
    }

    public void setAssembling_foil(Boolean assembling_foil) {
        this.assembling_foil = assembling_foil;
        setAssembling(assembling_foil);
    }

    public void setAssembling_visibleFormwork(Boolean assembling_visibleFormwork) {
        this.assembling_visibleFormwork = assembling_visibleFormwork;
        setAssembling(assembling_visibleFormwork);
    }

    public void setAssembling_sealingBand(Boolean assembling_sealingBand) {
        this.assembling_sealingBand = assembling_sealingBand;
        setAssembling(assembling_sealingBand);
    }

    public void setAssembling_counterBattens(Boolean assembling_counterBattens) {
        this.assembling_counterBattens = assembling_counterBattens;
        setAssembling(assembling_counterBattens);
    }

    public void setAssembling_battensOrFullFormwork(Boolean assembling_battensOrFullFormwork) {
        this.assembling_battensOrFullFormwork = assembling_battensOrFullFormwork;
        setAssembling(assembling_battensOrFullFormwork);
    }

}
