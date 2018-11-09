package com.redhelmet.alert2me.data;

import com.redhelmet.alert2me.data.database.entity.WatchZoneEntity;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.WatchZoneFilterType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class Mapper {

    @Inject
    public Mapper() {
    }

    public WatchZoneEntity map(EditWatchZones wz) {
        WatchZoneEntity entity = new WatchZoneEntity();
        entity.setId(wz.getId());
        entity.setDeviceId(wz.getDeviceId());
        entity.setSound(wz.getSound());
        entity.setAddress(wz.getAddress());
        entity.setName(wz.getName());
        entity.setRadius(wz.getRadius());
        entity.setWzType(wz.getWzType());
        entity.setFilterGroupId(wz.getFilterGroupId());
        entity.setEnable(wz.isEnable());
        entity.setProximity(wz.isProximity());
        entity.setDefault(wz.isDefault());
        entity.setNoEdit(wz.isNoEdit());
        entity.setShareCode(wz.getShareCode());
        entity.setGeom(wz.getGeom());

        if (wz.getFilter() != null) {
            WatchZoneEntity.WatchZoneFilter filter = new WatchZoneEntity.WatchZoneFilter();
            if (wz.getFilter().getWarning() != null) {
                List<WatchZoneFilterType> warning = wz.getFilter().getWarning().getTypes();
                filter.setWarning(warning);
            }
            if (wz.getFilter().getIncident() != null) {
                List<WatchZoneFilterType> incident = wz.getFilter().getIncident().getTypes();
                filter.setIncident(incident);
            }

            if (wz.getFilter().getRestriction() != null) {
                List<WatchZoneFilterType> restriction = wz.getFilter().getRestriction().getTypes();
                filter.setRestriction(restriction);
            }

            if (wz.getFilter().getSupport_service() != null) {
                List<WatchZoneFilterType> support_service = wz.getFilter().getSupport_service().getTypes();
                filter.setSupport_service(support_service);
            }

            entity.setFilter(filter);
        }

        return entity;
    }

    public EditWatchZones map(WatchZoneEntity entity) {
        EditWatchZones wz = new EditWatchZones();
        wz.setId(entity.getId());
        wz.setDeviceId(entity.getDeviceId());
        wz.setSound(entity.getSound());
        wz.setAddress(entity.getAddress());
        wz.setName(entity.getName());
        wz.setRadius(entity.getRadius());
        wz.setWzType(entity.getWzType());
        wz.setFilterGroupId(entity.getFilterGroupId());
        wz.setEnable(entity.isEnable());
        wz.setProximity(entity.isProximity());
        wz.setDefault(entity.isDefault());
        wz.setNoEdit(entity.isNoEdit());
        wz.setShareCode(entity.getShareCode());
        wz.setGeom(entity.getGeom());

        if (entity.getFilter() != null) {
            List<WatchZoneFilterType> warning = entity.getFilter().getWarning();
            List<WatchZoneFilterType> incident = entity.getFilter().getIncident();
            List<WatchZoneFilterType> restriction = entity.getFilter().getRestriction();
            List<WatchZoneFilterType> support_service = entity.getFilter().getSupport_service();

            EditWatchZones.WatchZoneFilter filter = new EditWatchZones.WatchZoneFilter();

            EditWatchZones.Filter warningFilter = new EditWatchZones.Filter();
            warningFilter.setTypes(warning);

            EditWatchZones.Filter incidentFilter = new EditWatchZones.Filter();
            incidentFilter.setTypes(incident);

            EditWatchZones.Filter restrictionFilter = new EditWatchZones.Filter();
            restrictionFilter.setTypes(restriction);

            EditWatchZones.Filter supportFilter = new EditWatchZones.Filter();
            supportFilter.setTypes(support_service);

            filter.setWarning(warningFilter);
            filter.setIncident(incidentFilter);
            filter.setRestriction(restrictionFilter);
            filter.setSupport_service(supportFilter);

            wz.setFilter(filter);
        }

        return wz;
    }

    public List<WatchZoneEntity> mapWzToWzEntities(List<EditWatchZones> list) {
        List<WatchZoneEntity> result = new ArrayList<>();
        for (EditWatchZones wz : list) {
            result.add(map(wz));
        }

        return result;
    }

    public List<EditWatchZones> mapWzEntitiesToWz(List<WatchZoneEntity> entities) {
        List<EditWatchZones> result = new ArrayList<>();
        for (WatchZoneEntity entity : entities) {
            result.add(map(entity));
        }

        return result;
    }
}
